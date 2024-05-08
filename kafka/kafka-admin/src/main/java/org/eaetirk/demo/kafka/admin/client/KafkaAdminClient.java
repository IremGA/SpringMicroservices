package org.eaetirk.demo.kafka.admin.client;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicListing;
import org.eaetirk.demo.config.KafkaConfigData;
import org.eaetirk.demo.config.RetryConfigData;
import org.eaetirk.demo.kafka.admin.exception.KafkaClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;


@Component
public class KafkaAdminClient {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaAdminClient.class);

    private final KafkaConfigData kafkaConfigData;

    private final RetryConfigData retryConfigData;

    private final AdminClient adminClient;

    private final RetryTemplate retryTemplate;

    private final WebClient webClient;

    public KafkaAdminClient(KafkaConfigData kafkaConfigData, RetryConfigData retryConfigData, AdminClient adminClient, RetryTemplate retryTemplate, WebClient webClient) {
        this.kafkaConfigData = kafkaConfigData;
        this.retryConfigData = retryConfigData;
        this.adminClient = adminClient;
        this.retryTemplate = retryTemplate;
        this.webClient = webClient;
    }

    public void createTopics(){
        CreateTopicsResult createTopicsResult;
        try {
            createTopicsResult = retryTemplate.execute(this::doCreateTopics);
        }catch (Throwable t){
            throw new KafkaClientException("Reached Max Number of Retry for creating Kafka Topics!", t);
        }
        checkTopicCreated();
    }
    public void checkTopicCreated(){
        Collection<TopicListing> topics = getTopics();
        int retryCount =1;
        Integer maxRetry = retryConfigData.getMaxAttempts();
        Integer multiplier = retryConfigData.getMultiplier().intValue();
        Long sleepTime= retryConfigData.getSleepTimeMs();

        for (String topic : kafkaConfigData.getTopicNamesToCreate()){
            if(!isTopicCreated(topics, topic)){
                checkMaxRetry(retryCount++, maxRetry);
                sleep(sleepTime);
                sleepTime *= multiplier;
                topics = getTopics();
            }
        }
    }

    private HttpStatusCode getSchemaRegistryStatus(){
        try {
            return webClient
                    .method(HttpMethod.GET)
                    .uri(kafkaConfigData.getSchemaRegistryUrl())
                    .exchangeToMono(clientResponse -> {
                        if(clientResponse.statusCode().is2xxSuccessful()){
                            return Mono.just(clientResponse.statusCode());
                        }else{
                            return Mono.just(HttpStatus.SERVICE_UNAVAILABLE);
                        }
                    })
                    .block();
        }catch (Exception e){

            LOG.error("Error while reaching to Kafka Schema Register Service : {}", e.getMessage());
            return HttpStatus.SERVICE_UNAVAILABLE;
        }

    }

    public void checkSchemaRegistry(){
        int retryCount =1;
        Integer maxRetry = retryConfigData.getMaxAttempts();
        Integer multiplier = retryConfigData.getMultiplier().intValue();
        Long sleepTime= retryConfigData.getSleepTimeMs();
        while (!getSchemaRegistryStatus().is2xxSuccessful()){
            checkMaxRetry(retryCount++, maxRetry);
            sleep(sleepTime);
            sleepTime *= multiplier;
        }
    }

    private void sleep(Long sleepTime) {
        try {
            Thread.sleep(sleepTime);
        }catch (InterruptedException e){
            throw new KafkaClientException("Error while sleeping for waiting new created topics!!");
        }
    }

    private void checkMaxRetry(int retry, Integer maxRetry) {
        if(retry > maxRetry){
            throw new KafkaClientException("Max Number of Retry count is reached for reading kafka topic(s)!");
        }
    }

    private boolean isTopicCreated(Collection<TopicListing> topics, String topicName) {
        if(topics == null){
         return false;
        }
        return topics.stream().anyMatch(topic -> topic.name().equals(topicName));
    }

    private CreateTopicsResult doCreateTopics(RetryContext retryContext) {
        List<String> topicNames = kafkaConfigData.getTopicNamesToCreate();
        LOG.info("Creating {} topic(s) , attempt {}", topicNames.size(), retryContext.getRetryCount());

        List<NewTopic> newTopicList = topicNames.stream().map(topic -> new NewTopic(
                topic.trim(),
                kafkaConfigData.getNumberOfPartitions(),
                kafkaConfigData.getReplicationFactor()
        )).toList();

        return adminClient.createTopics(newTopicList);
    }

    private Collection<TopicListing> getTopics(){
        Collection<TopicListing> topicListings;
        try {
            topicListings = retryTemplate.execute(this::doGetTopics);
        }catch(Throwable e){
            throw new KafkaClientException("Reached Max Number of Retry for reading Kafka Topics!", e);
        }
        return topicListings;
    }

    private Collection<TopicListing> doGetTopics(RetryContext retryContext) throws ExecutionException, InterruptedException {
        LOG.info("Reading kafka topic {} , attempt {}",kafkaConfigData.getTopicNamesToCreate().toArray(), retryContext.getRetryCount());
        Collection<TopicListing> topics = adminClient.listTopics().listings().get();
        if (topics != null){
            topics.forEach(topic -> LOG.debug("Topic with name {} ", topic.name()));
        }
        return topics;
    }

}
