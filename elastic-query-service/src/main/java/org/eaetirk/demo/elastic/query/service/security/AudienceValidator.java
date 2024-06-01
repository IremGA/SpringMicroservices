package org.eaetirk.demo.elastic.query.service.security;

import org.eaetirk.demo.config.ElasticQueryServiceConfigData;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Qualifier("elastic-query-service-audience-validator")
@Component
public class AudienceValidator  implements OAuth2TokenValidator<Jwt> {

    private final ElasticQueryServiceConfigData elasticQueryServiceConfigData;

    public AudienceValidator(ElasticQueryServiceConfigData elasticQueryServiceConfigData) {
        this.elasticQueryServiceConfigData = elasticQueryServiceConfigData;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {
        if (token.getAudience().contains(elasticQueryServiceConfigData.getCustomAudience())){
            return OAuth2TokenValidatorResult.success();
        }
        if (token.getAudience().contains(elasticQueryServiceConfigData.getClientOperationsCustomAudience())){
            return OAuth2TokenValidatorResult.success();
        }
        OAuth2Error audienceError = new OAuth2Error("invalid_token",
                "The required audience " + elasticQueryServiceConfigData.getCustomAudience()
                        +" is missing! ", null);

        return OAuth2TokenValidatorResult.failure(audienceError);
    }
}