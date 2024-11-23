package br.ueg.acervodigitalarquitetura.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IAbstractService<DTORequest, DTOResponse, DTOList, TYPE_PK> {
    Page<DTOList> listAll(Pageable pageable);
    DTOResponse create(DTORequest dtoCreate);
    DTOResponse update(TYPE_PK id, DTORequest dtoUpdate);
    DTOResponse getById(TYPE_PK id);
    DTOResponse deleteById(TYPE_PK id);
    Class<TYPE_PK> getEntityType();
}
