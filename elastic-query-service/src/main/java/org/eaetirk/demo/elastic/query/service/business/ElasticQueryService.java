package org.eaetirk.demo.elastic.query.service.business;

import org.eaetirk.demo.elastic.query.service.model.ElasticQueryServiceResponseModel;

import java.util.List;

public interface ElasticQueryService {

    ElasticQueryServiceResponseModel getDocumentById(String id);
    List<ElasticQueryServiceResponseModel> getDocumentsByText(String text);

    List<ElasticQueryServiceResponseModel> getAllDocuments();

}
