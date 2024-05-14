package org.eaetirk.demo.elastic.query.client.service.impl;

import org.eaetirk.demo.config.ElasticConfigData;
import org.eaetirk.demo.config.ElasticQueryConfigData;
import org.eaetirk.demo.elastic.model.index.impl.TwitterIndexModel;
import org.eaetirk.demo.elastic.query.client.exception.ElasticQueryClientException;
import org.eaetirk.demo.elastic.query.client.service.ElasticQueryClient;
import org.eaetirk.demo.elastic.query.client.util.ElasticQueryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@ConditionalOnProperty(name = "elastic-query-config.is-repository", havingValue = "false")
public class TwitterElasticQueryClient implements ElasticQueryClient<TwitterIndexModel> {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterElasticQueryClient.class);
    private final ElasticConfigData elasticConfigData;
    private final ElasticQueryConfigData elasticQueryConfigData;

    private  final ElasticsearchOperations elasticsearchOperations;

    private final ElasticQueryUtil<TwitterIndexModel> twitterIndexModelElasticQueryUtil;

    public TwitterElasticQueryClient(ElasticConfigData elasticConfigData,
                                     ElasticQueryConfigData elasticQueryConfigData,
                                     ElasticsearchOperations elasticsearchOperations,
                                     ElasticQueryUtil<TwitterIndexModel> twitterIndexModelElasticQueryUtil) {
        this.elasticConfigData = elasticConfigData;
        this.elasticQueryConfigData = elasticQueryConfigData;
        this.elasticsearchOperations = elasticsearchOperations;
        this.twitterIndexModelElasticQueryUtil = twitterIndexModelElasticQueryUtil;
    }

    @Override
    public TwitterIndexModel getIndexModelById(String id) {
        Query query = twitterIndexModelElasticQueryUtil.getSearchQueryById(id);
        SearchHit<TwitterIndexModel> twitterIndexModelSearchHit= elasticsearchOperations.searchOne(query, TwitterIndexModel.class, IndexCoordinates.of(elasticConfigData.getIndexName()));
        if(twitterIndexModelSearchHit == null){
            LOG.error("No document found at elastic search with id {} ", id);
            throw new ElasticQueryClientException("Query Result is null got the query getbyId: "+id );
        }
        LOG.info("Document with id {} retrieved successfully ", twitterIndexModelSearchHit.getId());
        return twitterIndexModelSearchHit.getContent();
    }

    @Override
    public List<TwitterIndexModel> getIndexModelByText(String text) {
        Query query = twitterIndexModelElasticQueryUtil.getSearchQueryByFieldText(elasticQueryConfigData.getTextField(), text);
        return listTwitterIndexModels(query, "There are {} documents retrieved from index {} for the text {}", elasticConfigData.getIndexName(), text);
    }

    private List<TwitterIndexModel> listTwitterIndexModels(Query query, String logMessage, Object... logParams) {
        SearchHits<TwitterIndexModel> twitterIndexModelSearchHit = elasticsearchOperations.search(query, TwitterIndexModel.class,IndexCoordinates.of(elasticConfigData.getIndexName()));
        LOG.info(logMessage, twitterIndexModelSearchHit.getTotalHits(),logParams);
        return twitterIndexModelSearchHit.get().map(SearchHit::getContent).toList();
    }

    @Override
    public List<TwitterIndexModel> getAllIndexModels() {
        Query query = twitterIndexModelElasticQueryUtil.getSearchQueryForAll();
        return listTwitterIndexModels(query,"There are {} documents retrieved ");
    }
}
