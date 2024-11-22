package br.ueg.genericarchitecture.service;

import java.util.List;

public interface IAbstractService<DTORequest, DTOResponse, DTOList, TYPE_PK> {
    List<DTOList> listAll();
    DTOResponse create(DTORequest dtoCreate);
    DTOResponse update(TYPE_PK id, DTORequest dtoUpdate);
    DTOResponse getById(TYPE_PK id);
    DTOResponse deleteById(TYPE_PK id);
    Class<TYPE_PK> getEntityType();
}
