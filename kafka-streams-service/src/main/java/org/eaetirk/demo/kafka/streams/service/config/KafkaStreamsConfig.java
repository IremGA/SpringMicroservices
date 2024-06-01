package org.eaetirk.demo.kafka.streams.service.config;

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.eaetirk.demo.config.KafkaConfigData;
import org.eaetirk.demo.config.KafkaStreamsConfigData;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class KafkaStreamsConfig {

    private final KafkaConfigData kafkaConfigData;
    private final KafkaStreamsConfigData kafkaStreamsConfigData;


    public KafkaStreamsConfig(KafkaConfigData kafkaConfigData, KafkaStreamsConfigData kafkaStreamsConfigData) {
        this.kafkaConfigData = kafkaConfigData;
        this.kafkaStreamsConfigData = kafkaStreamsConfigData;
    }

    @Bean
    @Qualifier("streamConfiguration")
    public Properties streamsConfiguration(){
        Properties streamConfiguration = new Properties();
        streamConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, kafkaStreamsConfigData.getApplicationID());
        streamConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfigData.getBootstrapServers());
        streamConfiguration.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, kafkaConfigData.getSchemaRegistryUrl());
        streamConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        streamConfiguration.put(StreamsConfig.STATE_DIR_CONFIG, kafkaStreamsConfigData.getStateFileLocation());
        streamConfiguration.put(StreamsConfig.REPLICATION_FACTOR_CONFIG, 3); // Set replication factor
        streamConfiguration.put(StreamsConfig.NUM_STANDBY_REPLICAS_CONFIG, 1); // Set number of standby replicas
        streamConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        return streamConfiguration;
    }
}
