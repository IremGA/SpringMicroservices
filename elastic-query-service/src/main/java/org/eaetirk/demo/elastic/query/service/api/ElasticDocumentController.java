package org.eaetirk.demo.elastic.query.service.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.eaetirk.demo.elastic.query.service.business.ElasticQueryService;
import org.eaetirk.demo.elastic.query.service.model.ElasticQueryServiceRequestModel;
import org.eaetirk.demo.elastic.query.service.model.ElasticQueryServiceResponseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/documents")
public class ElasticDocumentController {

    private static final Logger LOG = LoggerFactory.getLogger(ElasticDocumentController.class);

    private final ElasticQueryService elasticQueryService;

    public ElasticDocumentController(ElasticQueryService elasticQueryService) {
        this.elasticQueryService = elasticQueryService;
    }

    @GetMapping("/")
    public @ResponseBody ResponseEntity<List<ElasticQueryServiceResponseModel>> getAllDocuments(){
        List<ElasticQueryServiceResponseModel> response = elasticQueryService.getAllDocuments();
        LOG.info("Elastic Query returned {} records of documents", response.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public @ResponseBody ResponseEntity<ElasticQueryServiceResponseModel> getDocumentById(@PathVariable @NotEmpty String id){
        ElasticQueryServiceResponseModel elasticQueryServiceResponseModel = elasticQueryService.getDocumentById(id);
        LOG.info("ElasticSearch returned document with ID {} ", id);
        return ResponseEntity.ok(elasticQueryServiceResponseModel);
    }

    @PostMapping("/get-document-by-text")
    public @ResponseBody ResponseEntity<List<ElasticQueryServiceResponseModel>> getDocumentsByText(@RequestBody @Valid ElasticQueryServiceRequestModel requestModel){
        List<ElasticQueryServiceResponseModel> response = elasticQueryService.getDocumentsByText(requestModel.getText());
        LOG.info("ElasticSearch returned {} documents with Text {}  ",response.size(), requestModel.getText());
        return ResponseEntity.ok(response);
    }
}
