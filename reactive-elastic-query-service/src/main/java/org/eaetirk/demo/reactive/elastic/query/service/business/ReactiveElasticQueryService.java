package org.eaetirk.demo.reactive.elastic.query.service.business;

import org.eaetirk.demo.elastic.model.index.IndexModel;
import org.eaetirk.demo.elastic.model.index.impl.TwitterIndexModel;
import org.eaetirk.demo.reactive.elastic.query.service.model.ElasticQueryServiceResponseModel;
import reactor.core.publisher.Flux;

public interface ReactiveElasticQueryService{

    Flux<ElasticQueryServiceResponseModel> getDocumentsByText(String text);


}
