package org.eaetirk.demo.elastic.query.service.business.impl;

import org.eaetirk.demo.config.ElasticQueryServiceConfigData;
import org.eaetirk.demo.elastic.model.index.impl.TwitterIndexModel;
import org.eaetirk.demo.elastic.query.client.service.ElasticQueryClient;
import org.eaetirk.demo.elastic.query.service.business.ElasticQueryService;
import org.eaetirk.demo.elastic.query.service.config.QueryType;
import org.eaetirk.demo.elastic.query.service.constant.Constants;
import org.eaetirk.demo.elastic.query.service.exception.ElasticSearchQueryException;
import org.eaetirk.demo.elastic.query.service.model.ElasticQueryServiceAnalyticsResponseModel;
import org.eaetirk.demo.elastic.query.service.model.ElasticQueryServiceResponseModel;
import org.eaetirk.demo.elastic.query.service.model.assembler.ElasticQueryServiceResponseModelAssembler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class TwitterElasticQueryService implements ElasticQueryService {
    private static final Logger LOG = LoggerFactory.getLogger(TwitterElasticQueryService.class);

    private final ElasticQueryServiceResponseModelAssembler elasticQueryServiceResponseModelAssembler;

    private final ElasticQueryClient<TwitterIndexModel> elasticQueryClient;

    private final ElasticQueryServiceConfigData elasticQueryServiceConfigData;

    private final WebClient.Builder webClientBuilder;

    public TwitterElasticQueryService(ElasticQueryServiceResponseModelAssembler elasticQueryServiceResponseModelAssembler,
                                      ElasticQueryClient<TwitterIndexModel> elasticQueryClient,
                                      ElasticQueryServiceConfigData elasticQueryServiceConfigData,
                                      @Qualifier("webClientBuilder") WebClient.Builder webClientBuilder) {
        this.elasticQueryServiceResponseModelAssembler = elasticQueryServiceResponseModelAssembler;
        this.elasticQueryClient = elasticQueryClient;
        this.elasticQueryServiceConfigData = elasticQueryServiceConfigData;
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public ElasticQueryServiceResponseModel getDocumentById(String id) {
        LOG.info("Retrieving Document by ID {} ", id);
        return elasticQueryServiceResponseModelAssembler.toModel(elasticQueryClient.getIndexModelById(id));
    }

    @Override
    public List<ElasticQueryServiceResponseModel> getDocumentsByText(String text) {
        LOG.info("Retrieving Documents by text {} ", text);
        return elasticQueryServiceResponseModelAssembler.toModels(elasticQueryClient.getIndexModelByText(text));
    }

    @Override
    public ElasticQueryServiceAnalyticsResponseModel getDocumentsByText(String text, String accessToken) {
        LOG.info("Retrieving WordCount from Stream by text {} ", text);
        List<ElasticQueryServiceResponseModel> elasticQueryServiceAnalyticsResponseModels =
                elasticQueryServiceResponseModelAssembler.toModels(elasticQueryClient.getIndexModelByText(text));
        return ElasticQueryServiceAnalyticsResponseModel
                .builder()
                .elasticQueryServiceResponseModels(elasticQueryServiceAnalyticsResponseModels)
                .wordCount(getWordCountByText(text,accessToken))
                .build();
    }

    private Long getWordCountByText(String text, String accessToken) {
        LOG.info("Querying elastic search by test in stream");
        if(QueryType.KAFKA_STATE_STORE.getType().equals(elasticQueryServiceConfigData.getWebClient().getQueryType())){
            return getFromKafkaStore(text,accessToken).getWordCount();
        }
        return 0L;
    }

    private ElasticQueryServiceAnalyticsResponseModel getFromKafkaStore(String text, String accessToken) {
        ElasticQueryServiceConfigData.Query queryFromKafkaStateStore =
                elasticQueryServiceConfigData.getQueryFromKafkaStateStore();
        return retrieveResponse(text, accessToken, queryFromKafkaStateStore);
    }

    private ElasticQueryServiceAnalyticsResponseModel retrieveResponse(String text,
                                                                       String accessToken,
                                                                       ElasticQueryServiceConfigData.Query queryFromKafkaStateStore) {
        return webClientBuilder.defaultHeader(Constants.AUTHORIZATION, Constants.BEARER + accessToken).build()
                .method(HttpMethod.valueOf(queryFromKafkaStateStore.getMethod()))
                .uri(queryFromKafkaStateStore.getUri(), uriBuilder -> uriBuilder.build(text))
                .accept(MediaType.valueOf(queryFromKafkaStateStore.getAccept()))
                .retrieve()
                .onStatus(
                        s ->s.equals(HttpStatus.UNAUTHORIZED),
                        clientResponse -> Mono.just(new BadCredentialsException("Not Authenticated")))
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        clientResponse -> Mono.just(
                                new ElasticSearchQueryException(clientResponse.logPrefix(), clientResponse.statusCode()))
                        )
                .onStatus(
                        HttpStatusCode::is5xxServerError,
                        clientResponse -> Mono.just(new ElasticSearchQueryException(clientResponse.logPrefix(), clientResponse.statusCode()))
                ).bodyToMono(ElasticQueryServiceAnalyticsResponseModel.class)
                .log()
                .block();

    }

    @Override
    public List<ElasticQueryServiceResponseModel> getAllDocuments() {
        LOG.info("Retrieving all Documents ");
        return elasticQueryServiceResponseModelAssembler.toModels(elasticQueryClient.getAllIndexModels());
    }
}
