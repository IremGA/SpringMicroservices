package org.eaetirk.demo.elastic.query.client.util;

import org.eaetirk.demo.elastic.model.index.IndexModel;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.Queries;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class ElasticQueryUtil <T extends IndexModel> {

    public Query getSearchQueryById(String id){
        return NativeQuery
                .builder()
                .withIds(Collections.singleton(id))
                .build();
    }

    public Query getSearchQueryByFieldText(String field, String text){
        return NativeQuery.builder()
                .withQuery(Queries.matchQueryAsQuery(field, text, null, null))
                .build();
    }

    public Query getSearchQueryForAll(){
        return NativeQuery.builder()
                .withQuery(Queries.matchAllQueryAsQuery())
                .build();
    }

    public static  <T> List<T> getListFromIterable(Iterable<T> iterable){
        Queue<T> queue = new ConcurrentLinkedQueue<T>();
        iterable.forEach(queue::add);
        return queue.stream().toList();
    }
}
