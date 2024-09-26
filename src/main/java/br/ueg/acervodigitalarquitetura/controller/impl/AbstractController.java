package br.ueg.acervodigitalarquitetura.controller.impl;

import br.ueg.acervodigitalarquitetura.controller.IAbstractController;
import br.ueg.acervodigitalarquitetura.service.IAbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public class AbstractController<DTORequest, DTOResponse, DTOList, SERVICE extends IAbstractService<DTORequest, DTOResponse, DTOList, TYPE_PK>, TYPE_PK>
        implements IAbstractController<DTORequest, DTOResponse, DTOList, TYPE_PK> {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    protected SERVICE service;

    @PostMapping
    @Transactional
    public ResponseEntity<DTOResponse> create(@RequestBody DTORequest dtoCreate){
        DTOResponse resultDTO = service.create(dtoCreate);
        return ResponseEntity.status(HttpStatus.CREATED).body(resultDTO);
    }

    @PutMapping(path = "/{id}")
    @Transactional
    public ResponseEntity<DTOResponse> update(@PathVariable TYPE_PK id, @RequestBody DTORequest dto) {
        DTOResponse modelSaved = service.update(id, dto);
        return ResponseEntity.ok(modelSaved);
    }

    @DeleteMapping(path = "/{id}")
    @Transactional
    public ResponseEntity<DTOResponse> delete(@PathVariable TYPE_PK id){
        DTOResponse deleteDTO = service.deleteById(id);
        return ResponseEntity.ok(deleteDTO);
    }

    @GetMapping
    public ResponseEntity<List<DTOList>> listAll(){
        List<DTOList> listDTO = service.listAll();
        return ResponseEntity.ok(listDTO);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<DTOResponse> getById(@PathVariable TYPE_PK id){
        DTOResponse dtoResult = service.getById(id);
        return ResponseEntity.ok(dtoResult);
    }
}
