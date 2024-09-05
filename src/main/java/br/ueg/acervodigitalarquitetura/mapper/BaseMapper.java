package br.ueg.acervodigitalarquitetura.mapper;

import br.ueg.acervodigitalarquitetura.domain.BaseModel;
import org.mapstruct.IterableMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;

public interface BaseMapper<DTORequest, DTOResponse, DTOList, MODEL extends BaseModel<TYPE_PK>, TYPE_PK> {
    MODEL toModel(DTORequest dto);
    DTOResponse toDTO(MODEL model);


    @Named(value = "toDTOList")
    List<DTOList> toDtoList(List<MODEL> modelList);

    @IterableMapping(qualifiedByName = "toDTOList")
    void updateModelFromModel(@MappingTarget MODEL entity, MODEL updateEntity);
}
