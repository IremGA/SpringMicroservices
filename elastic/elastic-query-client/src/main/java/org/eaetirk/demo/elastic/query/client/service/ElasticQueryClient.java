package org.eaetirk.demo.elastic.query.client.service;

import org.eaetirk.demo.elastic.model.index.IndexModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ElasticQueryClient<T extends IndexModel> {
    T getIndexModelById(String id);
    List<T> getIndexModelByText(String text);
    List<T> getAllIndexModels();

}
