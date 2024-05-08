package org.eaetirk.demo.twitter.to.kafka.service.mapper;

import org.eaetirk.demo.kafka.avro.model.TwitterAvroModel;
import org.springframework.stereotype.Component;
import twitter4j.Status;

@Component
public class TwitterStatusToAvroMapper {

    public TwitterAvroModel getTwitterAvroModelFromStatus(Status status){
        return TwitterAvroModel
                .newBuilder()
                .setId(status.getId())
                .setUserId(status.getUser().getId())
                .setText(status.getText())
                .setCreatedAt(status.getCreatedAt().getTime())
                .build();
    }
}
