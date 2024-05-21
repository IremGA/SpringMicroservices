package org.eaetirk.demo.reactive.elastic.query.service.business.impl;

import org.eaetirk.demo.elastic.model.index.impl.TwitterIndexModel;
import org.eaetirk.demo.reactive.elastic.query.service.business.ReactiveElasticQueryClient;
import org.eaetirk.demo.reactive.elastic.query.service.business.ReactiveElasticQueryService;
import org.eaetirk.demo.reactive.elastic.query.service.mapper.ElasticToResponseModelMapper;
import org.eaetirk.demo.reactive.elastic.query.service.model.ElasticQueryServiceResponseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class TwitterElasticQueryService implements ReactiveElasticQueryService {
    private static final Logger LOG = LoggerFactory.getLogger(TwitterElasticQueryService.class);

    private final ReactiveElasticQueryClient<TwitterIndexModel> reactiveElasticQueryClient;

    private final ElasticToResponseModelMapper elasticToResponseModelMapper;

    public TwitterElasticQueryService(ReactiveElasticQueryClient<TwitterIndexModel> reactiveElasticQueryClient, ElasticToResponseModelMapper elasticToResponseModelMapper) {
        this.reactiveElasticQueryClient = reactiveElasticQueryClient;
        this.elasticToResponseModelMapper = elasticToResponseModelMapper;
    }




    @Override
    public Flux<ElasticQueryServiceResponseModel> getDocumentsByText(String text) {
        return reactiveElasticQueryClient
                .getIndexModelsByText(text)
                .flatMap(elasticToResponseModelMapper::getResponseModelMono)
                .doOnNext(businessObject -> LOG.info("Publishing BusinessObject: {}", businessObject))
                .doOnError(error -> LOG.error("Error occurred", error));
    }
}
