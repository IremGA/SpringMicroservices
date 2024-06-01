package org.eaetirk.demo.kafka.streams.service.runner.impl;

import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import jakarta.annotation.PreDestroy;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.eaetirk.demo.config.KafkaConfigData;
import org.eaetirk.demo.config.KafkaStreamsConfigData;
import org.eaetirk.demo.kafka.avro.model.TwitterAnalyticsAvroModel;
import org.eaetirk.demo.kafka.avro.model.TwitterAvroModel;
import org.eaetirk.demo.kafka.streams.service.runner.StreamsRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

@Component
public class KafkaStreamsRunner implements StreamsRunner<String, Long> {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaStreamsRunner.class);

    private static final String REGEX = "\\W+";

    private final KafkaStreamsConfigData kafkaStreamsConfigData;
    private final KafkaConfigData kafkaConfigData;
    private final Properties streamsConfiguration;

    private KafkaStreams kafkaStreams;

    private volatile ReadOnlyKeyValueStore<String, Long> keyValueStore;


    public KafkaStreamsRunner(KafkaStreamsConfigData kafkaStreamsConfigData, KafkaConfigData kafkaConfigData, @Qualifier("streamConfiguration") Properties properties) {
        this.kafkaStreamsConfigData = kafkaStreamsConfigData;
        this.kafkaConfigData = kafkaConfigData;
        this.streamsConfiguration = properties;
    }

    @PreDestroy
    public void close(){
        if(kafkaStreams != null){
            kafkaStreams.close();
        }
    }

    @Override
    public void start() {
        final Map<String, String> serdeConfig = Collections.singletonMap(
                kafkaConfigData.getSchemaRegistryUrlKey(),
                kafkaConfigData.getSchemaRegistryUrl()
        );


        final StreamsBuilder streamsBuilder = new StreamsBuilder();
        final KStream<Long, TwitterAvroModel> twitterAvroModelKSStream =
                getTwitterAvRoModelKStream(streamsBuilder,serdeConfig);
        createTopology(twitterAvroModelKSStream, serdeConfig);
        startStreaming(streamsBuilder);
    }

    @Override
    public Long getValueByKey(String word) {
        if(kafkaStreams != null && kafkaStreams.state() == KafkaStreams.State.RUNNING){
            if(keyValueStore == null){
                synchronized (this){
                    if(keyValueStore == null){
                        keyValueStore = kafkaStreams.store(StoreQueryParameters.fromNameAndType(
                                kafkaStreamsConfigData.getWordCountStoreName(),
                                QueryableStoreTypes.keyValueStore()));
                    }
                }
            }
            return keyValueStore.get(word.toLowerCase());
        }
        return 0L;
    }

    private void startStreaming(StreamsBuilder streamsBuilder) {
        final Topology topology = streamsBuilder.build();
        LOG.info("Defined topology: {}", topology.describe());
        kafkaStreams = new KafkaStreams(topology, streamsConfiguration);
        kafkaStreams.start();
        LOG.info("Kafka streaming started...");
    }

    private void createTopology(KStream<Long, TwitterAvroModel> twitterAvroModelKSStream,
                                Map<String, String> serdeConfig) {
        Pattern pattern = Pattern.compile(REGEX, Pattern.UNICODE_CHARACTER_CLASS);
        Serde<TwitterAnalyticsAvroModel> serdeTwitterAnalyticsModel = getSerdeAnalyticsModel(serdeConfig);
        twitterAvroModelKSStream.flatMapValues(value -> Arrays.asList(pattern.split(value.getText().toLowerCase())))
                .groupBy((key,word) -> word)
                .count(Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as(kafkaStreamsConfigData.getWordCountStoreName())
                        .withKeySerde(Serdes.String())
                        .withValueSerde(Serdes.Long()))
                .toStream()
                .map(mapToAnalyticsModel())
                .to(kafkaStreamsConfigData.getOutputTopicName(), Produced.with(Serdes.String(), serdeTwitterAnalyticsModel));
    }

    private KeyValueMapper< String, Long, KeyValue<? extends String,? extends TwitterAnalyticsAvroModel>> mapToAnalyticsModel() {
        return (word, count) ->{
          LOG.info("Sending to topic {} , word {} - count {}", kafkaStreamsConfigData.getOutputTopicName(), word, count);
          return new KeyValue<>(word, TwitterAnalyticsAvroModel.newBuilder()
                  .setWord(word)
                  .setWordCount(count)
                  .setCreatedAt(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))
                  .build());
        };
    }

    private Serde<TwitterAnalyticsAvroModel> getSerdeAnalyticsModel(Map<String, String> serdeConfig) {
        Serde<TwitterAnalyticsAvroModel> analyticsAvroModelSerde = new SpecificAvroSerde<>();
        analyticsAvroModelSerde.configure(serdeConfig, false);
        return analyticsAvroModelSerde;
    }

    private KStream<Long, TwitterAvroModel> getTwitterAvRoModelKStream(StreamsBuilder streamsBuilder, Map<String, String> serdeConfig) {
        final Serde<TwitterAvroModel> serdeTwitterAvroModel = new SpecificAvroSerde<>();
        serdeTwitterAvroModel.configure(serdeConfig, false);
        return streamsBuilder.stream(kafkaStreamsConfigData.getInputTopicName(), Consumed.with(Serdes.Long(), serdeTwitterAvroModel));
    }


}
