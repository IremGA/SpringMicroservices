package org.eaetirk.demo.reactive.elastic.query.service.business;

import org.eaetirk.demo.elastic.model.index.IndexModel;
import org.eaetirk.demo.elastic.model.index.impl.TwitterIndexModel;
import reactor.core.publisher.Flux;

public interface ReactiveElasticQueryClient<T extends IndexModel>{

    Flux<TwitterIndexModel> getIndexModelsByText(String text);


}
