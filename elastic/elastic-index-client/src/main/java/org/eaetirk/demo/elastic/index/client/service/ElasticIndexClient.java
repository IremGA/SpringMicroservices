package org.eaetirk.demo.elastic.index.client.service;

import org.eaetirk.demo.elastic.model.index.IndexModel;

import java.util.List;

public interface ElasticIndexClient <T extends IndexModel>{
    List<String> save(List<T> documents);
}
