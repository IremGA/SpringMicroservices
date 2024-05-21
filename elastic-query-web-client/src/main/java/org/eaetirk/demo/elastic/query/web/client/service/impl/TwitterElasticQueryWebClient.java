package org.eaetirk.demo.elastic.query.web.client.service.impl;

import org.eaetirk.demo.config.ElasticQueryWebClientConfigData;
import org.eaetirk.demo.elastic.query.web.client.exception.ElasticQueryWebClientException;
import org.eaetirk.demo.elastic.query.web.client.model.ElasticQueryWebClientRequestModel;
import org.eaetirk.demo.elastic.query.web.client.model.ElasticQueryWebClientResponseModel;
import org.eaetirk.demo.elastic.query.web.client.service.ElasticQueryWebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class TwitterElasticQueryWebClient implements ElasticQueryWebClient {
   private static final Logger LOG = LoggerFactory.getLogger(TwitterElasticQueryWebClient.class);

   private final WebClient.Builder webClientBuilder;

   private final ElasticQueryWebClientConfigData elasticQueryWebClientConfigData;

    public TwitterElasticQueryWebClient(@Qualifier("webClientBuilder") WebClient.Builder webClientBuilder, ElasticQueryWebClientConfigData elasticQueryWebClientConfigData) {
        this.webClientBuilder = webClientBuilder;
        this.elasticQueryWebClientConfigData = elasticQueryWebClientConfigData;
    }


    @Override
    public List<ElasticQueryWebClientResponseModel> getDataByText(ElasticQueryWebClientRequestModel requestModel) {
        LOG.info("Query by Text {} ",requestModel.getText() );
        return getWebClient(requestModel)
                .bodyToFlux(ElasticQueryWebClientResponseModel.class)
                .collectList().block();
    }

    private WebClient.ResponseSpec getWebClient(ElasticQueryWebClientRequestModel requestModel){
        return webClientBuilder
                .build()
                .method(HttpMethod.valueOf(elasticQueryWebClientConfigData.getQueryByText().getMethod()))
                .uri(elasticQueryWebClientConfigData.getQueryByText().getUri())
                .accept(MediaType.valueOf(elasticQueryWebClientConfigData.getQueryByText().getAccept()))
                .body(BodyInserters.fromPublisher(Mono.just(requestModel),createParametrizedTypeReference()))
                .retrieve()
                .onStatus(
                        HttpStatus -> HttpStatus.equals(org.springframework.http.HttpStatus.UNAUTHORIZED),
                        clientResponse -> Mono.just(new BadCredentialsException("Not Authenticated!"))
                )
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        cr -> Mono.just(new ElasticQueryWebClientException(cr.statusCode().toString()))
                )
                .onStatus(
                        HttpStatusCode::is5xxServerError,
                        clientResponse -> Mono.just(new Exception(clientResponse.statusCode().toString()))
                );
    }

    private <T>ParameterizedTypeReference<T> createParametrizedTypeReference(){
        return new ParameterizedTypeReference<T>() {
        };
    }
}
