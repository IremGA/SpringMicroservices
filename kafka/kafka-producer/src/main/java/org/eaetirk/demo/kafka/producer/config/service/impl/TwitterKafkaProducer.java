package org.eaetirk.demo.kafka.producer.config.service.impl;

import jakarta.annotation.PreDestroy;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.eaetirk.demo.kafka.avro.model.TwitterAvroModel;
import org.eaetirk.demo.kafka.producer.config.service.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class TwitterKafkaProducer implements KafkaProducer<Long, TwitterAvroModel> {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterKafkaProducer.class);

    private KafkaTemplate<Long, TwitterAvroModel> kafkaTemplate;

    public TwitterKafkaProducer(KafkaTemplate<Long, TwitterAvroModel> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PreDestroy
    public void close(){
        if(kafkaTemplate != null){
            LOG.info("Closing Kafka Producer!");
            kafkaTemplate.destroy();
        }
    }

    @Override
    public void send(String topicName, Long key, TwitterAvroModel message) {
        LOG.info("Sending message = '{}' to topic '{}'", message, topicName);
        CompletableFuture<SendResult<Long, TwitterAvroModel>> kafkaResultFuture =  kafkaTemplate.send(topicName,key,message);
        kafkaResultFuture.whenComplete((res, err) ->{

            if(err!=null){
                LOG.error("Error while sending message {} to topic {} ", message.toString(), topicName, err.getCause());
            }
            RecordMetadata metadata = res.getRecordMetadata();
            LOG.info("Received new metadata. Topic : {}; Partition {}; Offset {}; Timestamp {}, at time {}",
                    metadata.topic(),
                    metadata.partition(),
                    metadata.offset(),
                    metadata.timestamp(),
                    System.nanoTime());
        });
    }
}
