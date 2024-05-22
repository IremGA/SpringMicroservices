package org.eaetirk.demo.reactive.elastic.query.web.client.service;


import org.eaetirk.demo.reactive.elastic.query.web.client.model.ElasticQueryWebClientRequestModel;
import org.eaetirk.demo.reactive.elastic.query.web.client.model.ElasticQueryWebClientResponseModel;
import reactor.core.publisher.Flux;

public interface ElasticQueryWebClient {
    Flux<ElasticQueryWebClientResponseModel> getDataByText(ElasticQueryWebClientRequestModel requestModel);
}
