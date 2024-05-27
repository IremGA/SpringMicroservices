package org.eaetirk.demo.elastic.query.service.config;

import org.eaetirk.demo.elastic.query.service.security.TwitterQueryUserDetailsService;
import org.eaetirk.demo.elastic.query.service.security.TwitterQueryUserJWTConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    /**
     * Permit all operation
     * @param httpSecurity
     * @return
     * @throws Exception
     *
     *   @Bean
     *     public SecurityFilterChain webSecurityCustomizer(HttpSecurity httpSecurity) throws Exception{
     *         httpSecurity.authorizeHttpRequests(
     *                         requests -> requests
     *                                 .requestMatchers(new AntPathRequestMatcher("/**"))
     *                                 .permitAll()
     *                                 .anyRequest().authenticated())
     *                 .csrf(AbstractHttpConfigurer::disable) //not to get 403 Forbidden Error in Post operation
     *                 .httpBasic(Customizer.withDefaults());
     *         return httpSecurity.build();
     *     }
     *
     *     httpSecurity.authorizeHttpRequests(
     *                         requests -> requests
     *                                 .requestMatchers(new AntPathRequestMatcher("/**"))
     *                                 .permitAll()
     *                                 .anyRequest().authenticated())
     *                 .csrf(AbstractHttpConfigurer::disable)
     *                 .httpBasic(Customizer.withDefaults());
     */

    @Value("${security.paths-to-ignore}")
    private String[] pathsToIgnore;

    private final TwitterQueryUserDetailsService twitterQueryUserDetailsService;

    private final OAuth2ResourceServerProperties oAuth2ResourceServerProperties;

    public WebSecurityConfig(TwitterQueryUserDetailsService twitterQueryUserDetailsService, OAuth2ResourceServerProperties oAuth2ResourceServerProperties) {
        this.twitterQueryUserDetailsService = twitterQueryUserDetailsService;
        this.oAuth2ResourceServerProperties = oAuth2ResourceServerProperties;
    }


    @Bean
    public SecurityFilterChain webSecurityCustomizer(HttpSecurity httpSecurity) throws Exception{
        httpSecurity
                .authorizeHttpRequests(
                requests -> requests
                        .anyRequest().fullyAuthenticated())
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .oauth2ResourceServer((oauth2) -> oauth2.jwt(
                        jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(
                                twitterQueryUserJWTConverter())));
        return httpSecurity.build();
    }

    @Bean
    Converter<Jwt, ? extends AbstractAuthenticationToken> twitterQueryUserJWTConverter(){
        return new TwitterQueryUserJWTConverter(twitterQueryUserDetailsService);
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizerIgnore() {
        return (web) -> web.ignoring().requestMatchers(pathsToIgnore);
    }

    @Bean
    JwtDecoder jwtDecoder(@Qualifier("elastic-query-service-audience-validator")
                          OAuth2TokenValidator<Jwt> audienceValidator){
        NimbusJwtDecoder jwtDecoder = (NimbusJwtDecoder) JwtDecoders.fromOidcIssuerLocation(
                oAuth2ResourceServerProperties.getJwt().getIssuerUri());

        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(oAuth2ResourceServerProperties.getJwt().getIssuerUri());
        OAuth2TokenValidator<Jwt> withAudience = new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator);
        jwtDecoder.setJwtValidator(withAudience);
        return jwtDecoder;
    }
}
