package org.eaetirk.demo.twitter.to.kafka.service.listener;

import org.eaetirk.demo.config.KafkaConfigData;
import org.eaetirk.demo.kafka.avro.model.TwitterAvroModel;
import org.eaetirk.demo.kafka.producer.config.service.KafkaProducer;
import org.eaetirk.demo.twitter.to.kafka.service.mapper.TwitterStatusToAvroMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import twitter4j.Status;
import twitter4j.StatusAdapter;

@Component
public class TwitterKafkaStatusListener extends StatusAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterKafkaStatusListener.class);

    private  final KafkaConfigData kafkaConfigData;
    private final KafkaProducer<Long, TwitterAvroModel> kafkaProducer;

    private final TwitterStatusToAvroMapper twitterStatusToAvroMapper;

    public TwitterKafkaStatusListener(KafkaConfigData kafkaConfigData, KafkaProducer<Long, TwitterAvroModel> kafkaProducer, TwitterStatusToAvroMapper twitterStatusToAvroMapper) {
        this.kafkaConfigData = kafkaConfigData;
        this.kafkaProducer = kafkaProducer;
        this.twitterStatusToAvroMapper = twitterStatusToAvroMapper;
    }

    @Override
    public void onStatus(Status status){
        LOG.info("Twitter status with text {} ", status.getText());
        TwitterAvroModel twitterAvroModel = twitterStatusToAvroMapper.getTwitterAvroModelFromStatus(status);
        kafkaProducer.send(kafkaConfigData.getTopicName(), twitterAvroModel.getUserId(), twitterAvroModel);
    }
}
