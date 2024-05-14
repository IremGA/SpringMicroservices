package org.eaetirk.demo.elastic.query.service.exception.handler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ElasticSearchError {
    private String message;
    private String status;
    private String reason;
}
