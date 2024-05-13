package org.eaetirk.demo.kafka.to.elastic.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "org.eaetirk.demo" )
public class KafKaToElasticServiceApplication {
    public static void main(String[] args){
        SpringApplication.run(KafKaToElasticServiceApplication.class, args);
    }
}
