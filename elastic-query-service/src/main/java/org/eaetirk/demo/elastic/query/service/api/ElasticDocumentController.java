package org.eaetirk.demo.elastic.query.service.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.eaetirk.demo.elastic.query.service.business.ElasticQueryService;
import org.eaetirk.demo.elastic.query.service.model.ElasticQueryServiceRequestModel;
import org.eaetirk.demo.elastic.query.service.model.ElasticQueryServiceResponseModel;
import org.eaetirk.demo.elastic.query.service.model.ElasticQueryServiceResponseModelV2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/documents", produces = "application/vnd.api.v1+json")
public class ElasticDocumentController {

    private static final Logger LOG = LoggerFactory.getLogger(ElasticDocumentController.class);

    private final ElasticQueryService elasticQueryService;

    public ElasticDocumentController(ElasticQueryService elasticQueryService) {
        this.elasticQueryService = elasticQueryService;
    }
    @Value("${server.port}")
    private String port;

    @Operation(summary = "Gets All elastic Documents")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Success", content = {
                            @Content(mediaType = "application/vnd.api.v1+json",
                            schema = @Schema(implementation = ElasticQueryServiceResponseModel.class))
                    }),
                    @ApiResponse(responseCode = "400", description = "Not Found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @GetMapping("/")
    public @ResponseBody ResponseEntity<List<ElasticQueryServiceResponseModel>> getAllDocuments(){
        List<ElasticQueryServiceResponseModel> response = elasticQueryService.getAllDocuments();
        LOG.info("Elastic Query returned {} records of documents", response.size());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Gets All elastic Document by ID")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Success", content = {
                            @Content(mediaType = "application/vnd.api.v1+json",
                                    schema = @Schema(implementation = ElasticQueryServiceResponseModel.class))
                    }),
                    @ApiResponse(responseCode = "400", description = "Not Found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @GetMapping("/{id}")
    public @ResponseBody ResponseEntity<ElasticQueryServiceResponseModel> getDocumentById(@PathVariable @NotEmpty String id){
        ElasticQueryServiceResponseModel elasticQueryServiceResponseModel = elasticQueryService.getDocumentById(id);
        LOG.info("ElasticSearch returned document with ID {} ", id);
        return ResponseEntity.ok(elasticQueryServiceResponseModel);
    }

    @Operation(summary = "Gets All elastic Documents")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Success", content = {
                            @Content(mediaType = "application/vnd.api.v2+json",
                                    schema = @Schema(implementation = ElasticQueryServiceResponseModelV2.class))
                    }),
                    @ApiResponse(responseCode = "400", description = "Not Found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @GetMapping(value = "/{id}", produces = "application/vnd.api.v2+json")
    public @ResponseBody ResponseEntity<ElasticQueryServiceResponseModelV2> getDocumentByIdV2(@PathVariable @NotEmpty String id){
        ElasticQueryServiceResponseModel elasticQueryServiceResponseModel = elasticQueryService.getDocumentById(id);
        LOG.info("ElasticSearch returned document with ID {} ", id);

        return ResponseEntity.ok(toElasticResponseModelV2(elasticQueryServiceResponseModel));
    }

    @PreAuthorize("hasRole('APP_USER_ROLE') || hasAuthority('SCOPE_APP_USER_ROLE')")
    @Operation(summary = "Gets elastic Documents by text")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Success", content = {
                            @Content(mediaType = "application/vnd.api.v1+json",
                                    schema = @Schema(implementation = ElasticQueryServiceResponseModel.class))
                    }),
                    @ApiResponse(responseCode = "400", description = "Not Found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @PostMapping("/get-document-by-text")
    public @ResponseBody ResponseEntity<List<ElasticQueryServiceResponseModel>> getDocumentsByText(@RequestBody @Valid ElasticQueryServiceRequestModel requestModel){
        List<ElasticQueryServiceResponseModel> response = elasticQueryService.getDocumentsByText(requestModel.getText());
        LOG.info("ElasticSearch returned {} documents with Text {}, on port {}  ",response.size(), requestModel.getText(), port);
        return ResponseEntity.ok(response);
    }

    private ElasticQueryServiceResponseModelV2 toElasticResponseModelV2(ElasticQueryServiceResponseModel elasticQueryServiceResponseModel){
        ElasticQueryServiceResponseModelV2 elasticQueryServiceResponseModelV2 = ElasticQueryServiceResponseModelV2
                .builder()
                .test("testV2")
                .text(elasticQueryServiceResponseModel.getText())
                .userId(elasticQueryServiceResponseModel.getUserId())
                .createdAt(elasticQueryServiceResponseModel.getCreatedAt())
                .id(Long.parseLong(elasticQueryServiceResponseModel.getId()))
                .build();
        elasticQueryServiceResponseModelV2.add(elasticQueryServiceResponseModel.getLinks());
        return elasticQueryServiceResponseModelV2;
    }
}
