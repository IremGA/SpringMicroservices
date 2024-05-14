package org.eaetirk.demo.elastic.query.service.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class ElasticQueryServiceRequestModel {
    private String id;

    @NotEmpty
    private String text;

}
