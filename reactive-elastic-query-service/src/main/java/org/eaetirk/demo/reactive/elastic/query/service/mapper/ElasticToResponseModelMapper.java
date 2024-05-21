package org.eaetirk.demo.reactive.elastic.query.service.mapper;

import org.eaetirk.demo.elastic.model.index.impl.TwitterIndexModel;
import org.eaetirk.demo.reactive.elastic.query.service.model.ElasticQueryServiceResponseModel;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ElasticToResponseModelMapper {

    public ElasticQueryServiceResponseModel getResponseModel(TwitterIndexModel model) {

        return ElasticQueryServiceResponseModel
                .builder()
                .id(model.getId())
                .userId(model.getUserId())
                .text(model.getText())
                .createdAt(model.getCreatedAt())
                .build();
    }

    public Mono<ElasticQueryServiceResponseModel> getResponseModelMono(TwitterIndexModel model) {

        ElasticQueryServiceResponseModel elasticQueryServiceResponseModel= ElasticQueryServiceResponseModel
                .builder()
                .id(model.getId())
                .userId(model.getUserId())
                .text(model.getText())
                .createdAt(model.getCreatedAt())
                .build();
        return Mono.just(elasticQueryServiceResponseModel);
    }

    public List<ElasticQueryServiceResponseModel> getResponseModels(List<TwitterIndexModel> twitterIndexModels){
        return twitterIndexModels.stream().map(this::getResponseModel).collect(Collectors.toList());
    }
}
