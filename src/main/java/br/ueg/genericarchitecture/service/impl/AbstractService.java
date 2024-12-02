package br.ueg.genericarchitecture.service.impl;

import br.ueg.genericarchitecture.domain.GenericModel;
import br.ueg.genericarchitecture.exception.*;
import br.ueg.genericarchitecture.mapper.GenericMapper;
import br.ueg.genericarchitecture.enums.ValidationActionsEnum;
import br.ueg.genericarchitecture.enums.ApiErrorEnum;
import br.ueg.genericarchitecture.reflection.ApiReflectionUtils;
import br.ueg.genericarchitecture.service.IAbstractService;
import br.ueg.genericarchitecture.validation.IValidations;
import jakarta.persistence.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public abstract class AbstractService<DTORequest, DTOResponse, DTOList, MODEL extends GenericModel<TYPE_PK>, REPOSITORY extends JpaRepository<MODEL, TYPE_PK>,
        MAPPER extends GenericMapper<DTORequest, DTOResponse, DTOList, MODEL, TYPE_PK>, TYPE_PK>
        implements IAbstractService<DTORequest, MODEL, TYPE_PK> {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private REPOSITORY repository;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MAPPER mapper;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired(required = false)
    private List<IValidations<MODEL>> validations = new ArrayList<>();

    private Class<TYPE_PK> entityClass;

    public List<MODEL> listAll() {
        return repository.findAll();
    }

    public Page<MODEL> listAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public MODEL createFromDTO(DTORequest dtoCreate) {
        List<Message> messagesToThrow = new ArrayList<>();

        prepareToMapCreate(dtoCreate);
        validateToMapCreate(dtoCreate, messagesToThrow);
        validateMandatoryFieldsDTO(dtoCreate, messagesToThrow);
        MODEL data = mapper.toModel(dtoCreate);

        throwMessages(messagesToThrow);
        return this.create(data);
    }

    public MODEL create(MODEL dataCreate) {
        List<Message> messagesToThrow = new ArrayList<>();
        prepareToCreate(dataCreate);

        validateMandatoryFields(dataCreate, messagesToThrow);
        validateBusinessLogic(dataCreate, messagesToThrow);
        validateBusinessLogicForInsert(dataCreate, messagesToThrow);

        throwMessages(messagesToThrow);
        return repository.save(dataCreate);
    }

    public MODEL updateFromDTO(TYPE_PK id, DTORequest dtoUpdate) {
        List<Message> messagesToThrow = new ArrayList<>();

        prepareToMapUpdate(dtoUpdate);
        validateToMapUpdate(dtoUpdate, messagesToThrow);
        validateMandatoryFieldsDTO(dtoUpdate, messagesToThrow);

        throwMessages(messagesToThrow);

        MODEL dataUpdate = mapper.toModel(dtoUpdate);
        return this.update(id, dataUpdate);
    }

    public MODEL update(TYPE_PK id, MODEL dataUpdate) {
        List<Message> messagesToThrow = new ArrayList<>();
        var dataDB = validateIdModelExistsAndGet(id);

        prepareToUpdate(dataUpdate);

        validateMandatoryFields(dataUpdate, messagesToThrow);
        validateBusinessLogic(dataUpdate, messagesToThrow);
        validateBusinessLogicForUpdate(dataUpdate, messagesToThrow);

        throwMessages(messagesToThrow);

        mapper.updateModelFromModel(dataDB, dataUpdate);
        return repository.save(dataDB);
    }

    public MODEL getById(TYPE_PK id){
        return this.validateIdModelExistsAndGet(id);
    }

    public MODEL deleteById(TYPE_PK id){
        List<Message> messagesToThrow = new ArrayList<>();

        MODEL dataToRemove = this.validateIdModelExistsAndGet(id);

        validateBusinessLogicForDelete(dataToRemove, messagesToThrow);

        throwMessages(messagesToThrow);

        prepareToDelete(dataToRemove);
        this.repository.delete(dataToRemove);
        return dataToRemove;
    }

    public MODEL validateIdModelExistsAndGet(TYPE_PK id){
        if (!Objects.nonNull(id)) throw new ParameterRequiredException("id");

        Optional<MODEL> byId = repository.findById(id);
        if (byId.isPresent()) {
            return byId.get();
        } else {
            throw new DataException(ApiErrorEnum.NOT_FOUND, HttpStatus.NOT_FOUND);
        }
    }

    public void throwMessages(List<Message> messagesToThrow) {
        if (!messagesToThrow.isEmpty()) {
            MessageResponse messageResponse = new MessageResponse();
            messageResponse.setMessages(messagesToThrow);
            messageResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
            throw new BusinessException(messageResponse);
        }
    }

    public Class<TYPE_PK> getEntityType() {
        if(Objects.isNull(this.entityClass)){
            Type[] actualTypeArgumentsList = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments();
            for (Type argument : actualTypeArgumentsList) {
                for (AnnotatedType argumentInterface : (((Class<?>) argument).getAnnotatedInterfaces())) {
                    if (argumentInterface.getType() instanceof ParameterizedType && ((ParameterizedType) argumentInterface.getType()).getRawType().equals(GenericModel.class)) {
                        for (var annotation : ((Class<?>) argument).getDeclaredAnnotations()) {
                            if (annotation.annotationType().equals(Entity.class)) {
                                this.entityClass = ((Class<TYPE_PK>) argument);
                                return this.entityClass;
                            }
                        }
                    }
                }
            }
        }
        return this.entityClass;
    }

    protected void validateBusinessLogicForInsert(MODEL data, List<Message> messagesToThrow) {
        validations.forEach(v -> v.validate(data, ValidationActionsEnum.CREATE, messagesToThrow));
    }

    protected void validateBusinessLogicForUpdate(MODEL data, List<Message> messagesToThrow) {
        validations.forEach(v -> v.validate(data, ValidationActionsEnum.UPDATE, messagesToThrow));
    }

    protected void validateBusinessLogicForDelete(MODEL data, List<Message> messagesToThrow) {
        validations.forEach(v -> v.validate(data, ValidationActionsEnum.DELETE, messagesToThrow));
    }

    protected void validateBusinessLogic(MODEL data, List<Message> messagesToThrow) {
        validations.forEach(v -> v.validate(data, ValidationActionsEnum.GENERAL, messagesToThrow));
    }

    protected void validateMandatoryFields(MODEL data, List<Message> messagesToThrow) {
        validations.forEach(v -> v.validate(data, ValidationActionsEnum.GENERAL_MANDATORY, messagesToThrow));
    }

    private void validateMandatoryFieldsDTO(DTORequest dtoCreate, List<Message> messagesToThrow) {
        List<String> fieldsInvalid = new ArrayList<>();
        ApiReflectionUtils.validateMandatoryFields(dtoCreate, fieldsInvalid);

        if (!fieldsInvalid.isEmpty()) {
            for (String field : fieldsInvalid) {
                messagesToThrow.add(new Message(ApiErrorEnum.MANDATORY_FIELD, field));
            }
        }
    }

    protected void prepareToMapCreate(DTORequest dto) {}
    protected void validateToMapCreate(DTORequest dto, List<Message> messagesToThrow) {}
    protected void prepareToMapUpdate(DTORequest dto) {}
    protected void validateToMapUpdate(DTORequest dto, List<Message> messagesToThrow) {}

    protected abstract void prepareToCreate(MODEL data);
    protected abstract void prepareToUpdate(MODEL dataDB);
    protected abstract void prepareToDelete(MODEL dataDB);
}
