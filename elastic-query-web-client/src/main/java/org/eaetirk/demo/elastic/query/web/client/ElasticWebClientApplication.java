package org.eaetirk.demo.elastic.query.web.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "org.eaetirk.demo")
public class ElasticWebClientApplication {

    public static void main(String[] args){
        SpringApplication.run(ElasticWebClientApplication.class, args);

    }
}
