package br.ueg.acervodigitalarquitetura.service.impl;

import br.ueg.acervodigitalarquitetura.domain.BaseModel;
import br.ueg.acervodigitalarquitetura.mapper.BaseMapper;
import br.ueg.acervodigitalarquitetura.enums.ValidationActionsEnum;
import br.ueg.acervodigitalarquitetura.exception.DataException;
import br.ueg.acervodigitalarquitetura.enums.ErrorEnum;
import br.ueg.acervodigitalarquitetura.exception.ParameterRequiredException;
import br.ueg.acervodigitalarquitetura.service.IAbstractCrudService;
import br.ueg.acervodigitalarquitetura.validation.IValidations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public abstract class AbstractCrudService<DTORequest, DTOResponse, DTOList, MODEL extends BaseModel<TYPE_PK>, REPOSITORY extends JpaRepository<MODEL, TYPE_PK>,
        MAPPER extends BaseMapper<DTORequest, DTOResponse, DTOList, MODEL, TYPE_PK>, TYPE_PK>
        implements IAbstractCrudService<DTORequest, DTOResponse, DTOList, TYPE_PK> {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private REPOSITORY repository;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MAPPER mapper;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired(required = false)
    private List<IValidations<MODEL>> validations = new ArrayList<>();

    public List<DTOList> listAll() {
        return new ArrayList<>(mapper.toDtoList(repository.findAll()));
    }

    public DTOResponse create(DTORequest dtoCreate) {
        MODEL data = mapper.toModel(dtoCreate);
        prepareToCreate(data);
        validateMandatoryFields(data);
        validateBusinessLogic(data);
        validateBusinessLogicForInsert(data);
        return mapper.toDTO(repository.save(data));
    }

    public DTOResponse update(TYPE_PK id, DTORequest dtoUpdate) {
        var dataDB = validateIdModelExistsAndGet(id);
        var dataUpdate = mapper.toModel(dtoUpdate);

        mapper.updateModelFromModel(dataDB, dataUpdate);
        prepareToUpdate(dataDB);
        validateMandatoryFields(dataDB);
        validateBusinessLogic(dataDB);
        validateBusinessLogicForUpdate(dataDB);
        return mapper.toDTO(repository.save(dataDB));
    }

    public DTOResponse getById(TYPE_PK id){
        return mapper.toDTO(this.validateIdModelExistsAndGet(id));
    }

    public DTOResponse deleteById(TYPE_PK id){
        MODEL dataToRemove = this.validateIdModelExistsAndGet(id);
        validateBusinessLogicForDelete(dataToRemove);
        this.repository.delete(dataToRemove);
        prepareToDelete(dataToRemove);
        return mapper.toDTO(dataToRemove);
    }

    public MODEL validateIdModelExistsAndGet(TYPE_PK id){
        if (!Objects.nonNull(id)) throw new ParameterRequiredException("id");

        Optional<MODEL> byId = repository.findById(id);
        if (byId.isPresent()) {
            return byId.get();
        } else {
            throw new DataException(ErrorEnum.NOT_FOUND);
        }
    }

    protected void validateBusinessLogicForInsert(MODEL data) {
        validations.forEach(v -> v.validate(data, ValidationActionsEnum.CREATE));
    }

    protected void validateBusinessLogicForUpdate(MODEL data) {
        validations.forEach(v -> v.validate(data, ValidationActionsEnum.UPDATE));
    }

    protected void validateBusinessLogicForDelete(MODEL data) {
        validations.forEach(v -> v.validate(data, ValidationActionsEnum.DELETE));
    }

    protected void validateBusinessLogic(MODEL data) {
        validations.forEach(v -> v.validate(data, ValidationActionsEnum.GENERAL));
    }

    protected void validateMandatoryFields(MODEL data) {
        validations.forEach(v -> v.validate(data, ValidationActionsEnum.GENERAL_MANDATORY));
    }

    protected abstract void prepareToCreate(MODEL data);
    protected abstract void prepareToUpdate(MODEL dataDB);
    protected abstract void prepareToDelete(MODEL dataDB);
}
