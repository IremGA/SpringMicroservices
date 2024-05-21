package org.eaetirk.demo.reactive.elastic.query.service.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class ElasticQueryServiceResponseModelV2{
    private Long id;
    private String text;
    private Long userId;
    private String test;
    private ZonedDateTime createdAt;
}
