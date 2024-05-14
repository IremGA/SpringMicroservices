package org.eaetirk.demo.kafka.to.elastic.service.mapper;

import org.eaetirk.demo.elastic.model.index.impl.TwitterIndexModel;
import org.eaetirk.demo.kafka.avro.model.TwitterAvroModel;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AvroToElasticModelMapper {

    public List<TwitterIndexModel> getElasticModels(List<TwitterAvroModel> avroModels){

        return avroModels.stream()
                .map(avroModel -> TwitterIndexModel.builder()
                        .userId(avroModel.getUserId())
                        .id(String.valueOf(avroModel.getId()))
                        .text(avroModel.getText())
                        .createdAt(Instant.ofEpochMilli(avroModel.getCreatedAt()).atZone(ZoneId.of("UTC"))).build()).collect(Collectors.toList());
    }

}
