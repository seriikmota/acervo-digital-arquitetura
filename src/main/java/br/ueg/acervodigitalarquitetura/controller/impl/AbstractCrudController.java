package br.ueg.acervodigitalarquitetura.controller.impl;

import br.ueg.acervodigitalarquitetura.controller.IAbstractCrudController;
import br.ueg.acervodigitalarquitetura.service.IAbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

public class AbstractCrudController<DTORequest, DTOResponse, DTOList, SERVICE extends IAbstractService<DTORequest, DTOResponse, DTOList, TYPE_PK>, TYPE_PK>
        implements IAbstractCrudController<DTORequest, DTOResponse, DTOList, TYPE_PK> {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    protected SERVICE service;

    @PostMapping
    @Transactional
    @PreAuthorize(value = "hasRole(#root.this.getRoleName('CREATE'))")
    public ResponseEntity<DTOResponse> create(@RequestBody DTORequest dtoCreate){
        DTOResponse resultDTO = service.create(dtoCreate);
        return ResponseEntity.status(HttpStatus.CREATED).body(resultDTO);
    }

    @PutMapping(path = "/{id}")
    @Transactional
    @PreAuthorize(value = "hasRole(#root.this.getRoleName('UPDATE'))")
    public ResponseEntity<DTOResponse> update(@PathVariable TYPE_PK id, @RequestBody DTORequest dto) {
        DTOResponse modelSaved = service.update(id, dto);
        return ResponseEntity.ok(modelSaved);
    }

    @DeleteMapping(path = "/{id}")
    @Transactional
    @PreAuthorize(value = "hasRole(#root.this.getRoleName('DELETE'))")
    public ResponseEntity<DTOResponse> delete(@PathVariable TYPE_PK id){
        DTOResponse deleteDTO = service.deleteById(id);
        return ResponseEntity.ok(deleteDTO);
    }

    @GetMapping
    @PreAuthorize("hasRole(#root.this.getRoleName('LISTALL'))")
    public ResponseEntity<Page<DTOList>> listAll(Pageable pageable){
        Page<DTOList> listDTO = service.listAll(pageable);
        return ResponseEntity.ok(listDTO);
    }

    @GetMapping(path = "/{id}")
    @PreAuthorize(value = "hasRole(#root.this.getRoleName('READ'))")
    public ResponseEntity<DTOResponse> getById(@PathVariable TYPE_PK id){
        DTOResponse dtoResult = service.getById(id);
        return ResponseEntity.ok(dtoResult);
    }

    public String getRoleName(String action){
        return "ROLE_".concat(this.service.getEntityType().getSimpleName().toUpperCase().concat("_"+action.toUpperCase()));
    }
}
