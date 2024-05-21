package org.eaetirk.demo.elastic.query.web.client.service;

import org.eaetirk.demo.elastic.query.web.client.model.ElasticQueryWebClientRequestModel;
import org.eaetirk.demo.elastic.query.web.client.model.ElasticQueryWebClientResponseModel;

import java.util.List;

public interface ElasticQueryWebClient {
    List<ElasticQueryWebClientResponseModel> getDataByText(ElasticQueryWebClientRequestModel requestModel);
}
