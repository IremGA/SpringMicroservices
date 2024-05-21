package org.eaetirk.demo.reactive.elastic.query.service.exception.handler;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ElasticSearchError {
    private String message;
    private String status;
    private String reason;
}
