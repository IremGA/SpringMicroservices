package org.eaetirk.demo.elastic.model.index.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.eaetirk.demo.elastic.model.index.IndexModel;
import org.eaetirk.demo.elastic.model.index.util.CustomZonedDateTimeConverter;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.ZonedDateTime;

@Builder
@Data
@Document(indexName = "#{@elasticConfigData.indexName}")
public class TwitterIndexModel implements IndexModel {

    @JsonProperty
    private String id;

    @JsonProperty
    private Long userId;

    @JsonProperty
    private String text;

    @Field(type = FieldType.Date, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSX||uuuu-MM-dd'T'HH:mm:ss.SSS", format = {})
    @ValueConverter(CustomZonedDateTimeConverter.class)
    @JsonProperty
    private ZonedDateTime createdAt;

}
