package org.eaetirk.demo.reactive.elastic.query.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
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
     */

    @Bean
    public SecurityWebFilterChain webSecurityCustomizer(ServerHttpSecurity httpSecurity) throws Exception{
        httpSecurity.authorizeExchange((authorize) -> authorize
                .anyExchange().permitAll());
        httpSecurity.csrf(ServerHttpSecurity.CsrfSpec::disable);
        return httpSecurity.build();
    }

}
