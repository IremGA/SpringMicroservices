package org.eaetirk.demo.elastic.query.client.service.impl;

import org.eaetirk.demo.elastic.model.index.impl.TwitterIndexModel;
import org.eaetirk.demo.elastic.query.client.exception.ElasticQueryClientException;
import org.eaetirk.demo.elastic.query.client.repository.TwitterElasticSearchRepository;
import org.eaetirk.demo.elastic.query.client.service.ElasticQueryClient;
import org.eaetirk.demo.elastic.query.client.util.ElasticQueryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

//@Primary
@Service
@ConditionalOnProperty(name = "elastic-query-config.is-repository", havingValue = "true", matchIfMissing = true)
public class TwitterElasticRepositoryQueryClient implements ElasticQueryClient<TwitterIndexModel> {

    Logger logger = LoggerFactory.getLogger(TwitterElasticRepositoryQueryClient.class);
    private final TwitterElasticSearchRepository twitterElasticSearchRepository;

    public TwitterElasticRepositoryQueryClient(TwitterElasticSearchRepository twitterElasticSearchRepository) {
        this.twitterElasticSearchRepository = twitterElasticSearchRepository;
    }

    @Override
    public TwitterIndexModel getIndexModelById(String id) {
        Optional<TwitterIndexModel>  optionalResult = twitterElasticSearchRepository.findById(id);
        logger.info("Document with ID {} is retrieved successfully ", optionalResult.orElseThrow(()->new ElasticQueryClientException("No document found at elastic search with id " + id)).getId());
        return optionalResult.get();
    }

    @Override
    public List<TwitterIndexModel> getIndexModelByText(String text) {
        List<TwitterIndexModel> searchResult = twitterElasticSearchRepository.findByText(text);
        logger.info("There are {} documents with text {} retrieved successfully ", searchResult.size(), text);
        return searchResult;
    }

    @Override
    public List<TwitterIndexModel> getAllIndexModels() {
        List<TwitterIndexModel> searchResult = ElasticQueryUtil.getListFromIterable(twitterElasticSearchRepository.findAll());
        logger.info("There are {} documents retrieved successfully ", searchResult.size());
        return searchResult;
    }
}
