package org.eaetirk.demo.kafka.streams.service;

import org.eaetirk.demo.kafka.streams.service.init.StreamInitializer;
import org.eaetirk.demo.kafka.streams.service.runner.StreamsRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan(basePackages = "org.eaetirk.demo")
public class KafkaStreamsServiceApplication  implements CommandLineRunner {
    private static final Logger LOG = LoggerFactory.getLogger(KafkaStreamsServiceApplication.class);
    private final StreamInitializer streamInitializer;

    private final StreamsRunner streamsRunner;

    public KafkaStreamsServiceApplication(StreamInitializer streamInitializer, StreamsRunner streamsRunner) {

        this.streamInitializer = streamInitializer;
        this.streamsRunner = streamsRunner;
    }

    public static void main(String[] args){
        SpringApplication.run(KafkaStreamsServiceApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        LOG.info("App starts...");
        streamInitializer.init();
        streamsRunner.start();
    }
}