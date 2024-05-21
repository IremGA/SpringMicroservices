package org.eaetirk.demo.reactive.elastic.query.service.api;

import org.eaetirk.demo.reactive.elastic.query.service.business.ReactiveElasticQueryService;
import org.eaetirk.demo.reactive.elastic.query.service.model.ElasticQueryServiceRequestModel;
import org.eaetirk.demo.reactive.elastic.query.service.model.ElasticQueryServiceResponseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping(value = "/documents")
public class ReactiveElasticDocumentController {

    private static final Logger LOG = LoggerFactory.getLogger(ReactiveElasticDocumentController.class);

    private final ReactiveElasticQueryService elasticQueryService;

    public ReactiveElasticDocumentController(ReactiveElasticQueryService elasticQueryService) {
        this.elasticQueryService = elasticQueryService;
    }

    @PostMapping(value = "/get-doc-by-text",
    produces = MediaType.TEXT_EVENT_STREAM_VALUE,
    consumes = MediaType.APPLICATION_JSON_VALUE)
    public Flux<ElasticQueryServiceResponseModel> getDocumentsByText(@RequestBody ElasticQueryServiceRequestModel requestModel){
        Flux<ElasticQueryServiceResponseModel> response = elasticQueryService.getDocumentsByText(requestModel.getText());
        LOG.info("Reactive ElasticSearch returned {} documents with Text {}  ",response.log(), requestModel.getText());
        // Logging the request and subscribing to the response for logging
        return response.doOnNext(resp -> LOG.info("Received document: {}", resp))
                .doOnError(e -> LOG.error("Error occurred while fetching documents", e))
                .doOnComplete(() -> LOG.info("Request completed successfully."))
                .doOnCancel(() -> LOG.warn("Request canceled."));
    }

}
