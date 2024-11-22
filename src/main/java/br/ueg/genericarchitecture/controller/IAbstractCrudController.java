package br.ueg.genericarchitecture.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


public interface IAbstractCrudController<DTORequest, DTOResponse, DTOList, TYPE_PK> {
    @Operation(description = "Endpoint to register a object", responses = {
            @ApiResponse(responseCode = "201", content = @Content(schema = @Schema())),
            @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(hidden = true))),
    })
    ResponseEntity<DTOResponse> create(@RequestBody DTORequest dto);

    @Operation(description = "Endpoint to edit a object", responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(hidden = true))),
    })
    ResponseEntity<DTOResponse> update(@PathVariable TYPE_PK id, @RequestBody DTORequest dto);

    @Operation(description = "Endpoint to remove a object", responses = {
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(hidden = true)))
    })
    ResponseEntity<DTOResponse> delete(@PathVariable TYPE_PK id);

    @Operation(description = "Endpoint to list all objects", responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(hidden = true)))
    })
    ResponseEntity<List<DTOList>> listAll();

    @Operation(description = "Endpoint to search for an object by primary key", responses = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(hidden = true)))
    })
    ResponseEntity<DTOResponse> getById(@PathVariable TYPE_PK id);
}
