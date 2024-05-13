package org.eaetirk.demo.elastic.index.client.service.impl;


import org.eaetirk.demo.config.ElasticConfigData;
import org.eaetirk.demo.elastic.index.client.repository.TwitterElasticSearchIndexRepository;
import org.eaetirk.demo.elastic.index.client.service.ElasticIndexClient;
import org.eaetirk.demo.elastic.model.index.impl.TwitterIndexModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@ConditionalOnProperty(name = "elastic-config.is-repository", havingValue = "true", matchIfMissing = true)
public class TwitterElasticSearchIndexRepositoryClient implements ElasticIndexClient<TwitterIndexModel> {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterElasticSearchIndexRepositoryClient.class);
    private  final TwitterElasticSearchIndexRepository twitterElasticSearchIndexRepository;

    public TwitterElasticSearchIndexRepositoryClient(TwitterElasticSearchIndexRepository twitterElasticSearchIndexRepository) {
        this.twitterElasticSearchIndexRepository = twitterElasticSearchIndexRepository;
    }

    @Override
    public List<String> save(List documents) {
        List<String> documentIds = new ArrayList<>();
        for (Object o : twitterElasticSearchIndexRepository.saveAll(documents)) {
            TwitterIndexModel indexModel = (TwitterIndexModel) o;
            documentIds.add(indexModel.getId());
        }
        LOG.info("Documents indexed successfully with type : {} and ids: {} ", TwitterIndexModel.class.getName(), documentIds);
        return documentIds;
    }
}
