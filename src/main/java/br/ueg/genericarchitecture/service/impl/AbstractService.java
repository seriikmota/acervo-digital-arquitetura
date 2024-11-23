package br.ueg.genericarchitecture.service.impl;

import br.ueg.genericarchitecture.domain.GenericModel;
import br.ueg.genericarchitecture.exception.*;
import br.ueg.genericarchitecture.mapper.GenericMapper;
import br.ueg.genericarchitecture.enums.ValidationActionsEnum;
import br.ueg.genericarchitecture.enums.ApiErrorEnum;
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
        implements IAbstractService<DTORequest, DTOResponse, DTOList, TYPE_PK> {

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

    public Page<DTOList> listAll(Pageable pageable) {
        return repository.findAll(pageable).map(obj -> mapper.toDTOList(obj));
    }

    public DTOResponse create(DTORequest dtoCreate) {
        List<Message> messagesToThrow = new ArrayList<>();

        prepareToMapCreate(dtoCreate);
        validateToMapCreate(dtoCreate, messagesToThrow);
        MODEL data = mapper.toModel(dtoCreate);

        prepareToCreate(data);

        validateMandatoryFields(data, messagesToThrow);
        validateBusinessLogic(data, messagesToThrow);
        validateBusinessLogicForInsert(data, messagesToThrow);

        throwMessages(messagesToThrow);
        return mapper.toDTO(repository.save(data));
    }

    public DTOResponse update(TYPE_PK id, DTORequest dtoUpdate) {
        List<Message> messagesToThrow = new ArrayList<>();
        var dataDB = validateIdModelExistsAndGet(id);

        prepareToMapUpdate(dtoUpdate);
        validateToMapUpdate(dtoUpdate, messagesToThrow);
        var dataUpdate = mapper.toModel(dtoUpdate);

        mapper.updateModelFromModel(dataDB, dataUpdate);
        prepareToUpdate(dataDB);

        validateMandatoryFields(dataDB, messagesToThrow);
        validateBusinessLogic(dataDB, messagesToThrow);
        validateBusinessLogicForUpdate(dataDB, messagesToThrow);

        throwMessages(messagesToThrow);
        return mapper.toDTO(repository.save(dataDB));
    }

    public DTOResponse getById(TYPE_PK id){
        return mapper.toDTO(this.validateIdModelExistsAndGet(id));
    }

    public DTOResponse deleteById(TYPE_PK id){
        List<Message> messagesToThrow = new ArrayList<>();

        MODEL dataToRemove = this.validateIdModelExistsAndGet(id);

        validateBusinessLogicForDelete(dataToRemove, messagesToThrow);

        throwMessages(messagesToThrow);

        prepareToDelete(dataToRemove);
        this.repository.delete(dataToRemove);
        return mapper.toDTO(dataToRemove);
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

    protected void prepareToMapCreate(DTORequest dto) {}
    protected void validateToMapCreate(DTORequest dto, List<Message> messagesToThrow) {}
    protected void prepareToMapUpdate(DTORequest dto) {}
    protected void validateToMapUpdate(DTORequest dto, List<Message> messagesToThrow) {}

    protected abstract void prepareToCreate(MODEL data);
    protected abstract void prepareToUpdate(MODEL dataDB);
    protected abstract void prepareToDelete(MODEL dataDB);
}
