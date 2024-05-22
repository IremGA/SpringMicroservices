package org.eaetirk.demo.reactive.elastic.query.web.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "org.eaetirk.demo")
public class ReactiveElasticWebClientApplication {

    public static void main(String[] args){
        SpringApplication.run(ReactiveElasticWebClientApplication.class, args);

    }
}
