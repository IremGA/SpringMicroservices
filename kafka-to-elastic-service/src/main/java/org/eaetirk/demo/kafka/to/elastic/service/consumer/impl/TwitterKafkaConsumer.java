package org.eaetirk.demo.kafka.to.elastic.service.consumer.impl;

import org.eaetirk.demo.config.KafkaConfigData;
import org.eaetirk.demo.config.KafkaConsumerData;
import org.eaetirk.demo.elastic.index.client.service.ElasticIndexClient;
import org.eaetirk.demo.elastic.model.index.impl.TwitterIndexModel;
import org.eaetirk.demo.kafka.admin.client.KafkaAdminClient;
import org.eaetirk.demo.kafka.avro.model.TwitterAvroModel;
import org.eaetirk.demo.kafka.to.elastic.service.consumer.KafkaConsumer;
import org.eaetirk.demo.kafka.to.elastic.service.mapper.AvroToElasticModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class TwitterKafkaConsumer implements KafkaConsumer<Long, TwitterAvroModel> {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterKafkaConsumer.class);

    private  final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
    private final KafkaAdminClient kafkaAdminClient;

    private final KafkaConfigData kafkaConfigData;

    private final AvroToElasticModelMapper avroToElasticModelMapper;

    private  final KafkaConsumerData kafkaConsumerData;

    private final ElasticIndexClient<TwitterIndexModel> twitterIndexModelElasticIndexClient;

    public TwitterKafkaConsumer(KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry, KafkaAdminClient kafkaAdminClient, KafkaConfigData kafkaConfigData, AvroToElasticModelMapper avroToElasticModelMapper, KafkaConsumerData kafkaConsumerData, ElasticIndexClient<TwitterIndexModel> twitterIndexModelElasticIndexClient) {
        this.kafkaListenerEndpointRegistry = kafkaListenerEndpointRegistry;
        this.kafkaAdminClient = kafkaAdminClient;
        this.kafkaConfigData = kafkaConfigData;
        this.avroToElasticModelMapper = avroToElasticModelMapper;
        this.kafkaConsumerData = kafkaConsumerData;
        this.twitterIndexModelElasticIndexClient = twitterIndexModelElasticIndexClient;
    }

    @EventListener
    public void onApplicationStartup(ApplicationStartedEvent applicationStartedEvent){
        kafkaAdminClient.checkTopicCreated();
        LOG.info("Topics with the name {} is ready for operation! ", kafkaConfigData.getTopicNamesToCreate().toArray());
        Objects.requireNonNull(kafkaListenerEndpointRegistry.getListenerContainer(kafkaConsumerData.getConsumerGroupId())).start();

    }

    @Override
    @KafkaListener(id = "${kafka-consumer-config.consumer-group-id}", topics = "${kafka-config.topic-name}")
    public void receive(@Payload List messages,
                        @Header(KafkaHeaders.RECEIVED_KEY) List keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) List partitions,
                        @Header(KafkaHeaders.OFFSET) List offsets) {

        LOG.info("{} message/s received with keys {}, partitions {} and offsets {}, "+ "sending it to elastic : Thread Name {} "
                ,messages.size(), keys.toString(), partitions.toString(), offsets.toString(), Thread.currentThread().getName() );
        List<TwitterIndexModel> twitterIndexModels = avroToElasticModelMapper.getElasticModels(messages);
        List<String> documentIds = twitterIndexModelElasticIndexClient.save(twitterIndexModels);
        LOG.info("Documents are saved to elastic search with IDs {} ", documentIds.toString());


    }
}
