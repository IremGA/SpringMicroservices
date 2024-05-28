package org.eaetirk.demo.elastic.query.web.client.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    private final ClientRegistrationRepository clientRegistrationRepository;

    private final static Logger LOG = LoggerFactory.getLogger(WebSecurityConfig.class);

    @Value("${security.logout-success-url}")
    private String logout_success_url;

    private final static String GROUPS_CLAIM = "groups";
    private final static String ROLE = "ROLE_";

    public WebSecurityConfig( ClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    OidcClientInitiatedLogoutSuccessHandler oidcClientInitiatedLogoutSuccessHandler(){
        OidcClientInitiatedLogoutSuccessHandler oidcClientInitiatedLogoutSuccessHandler = new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
        oidcClientInitiatedLogoutSuccessHandler.setPostLogoutRedirectUri(logout_success_url);
        return oidcClientInitiatedLogoutSuccessHandler;
    }

    /**
     * Its method oauth2Client is not related to client configurations, but how and where the server/application should handle them.
     *
     * Example:
     *
     * Which clients have been authorized
     * Where to store authorized clients
     * How to authorize clients
     * How to remove an old authorized client
     * @param httpSecurity
     * @return
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain webSecurityCustomizer(HttpSecurity httpSecurity) throws Exception{
        httpSecurity.authorizeHttpRequests(
                        requests -> requests
                                .requestMatchers(new AntPathRequestMatcher("/"))
                                .permitAll()
                                .anyRequest().fullyAuthenticated())
                .logout(logoutCustomizer->logoutCustomizer.logoutSuccessHandler(oidcClientInitiatedLogoutSuccessHandler()))
                .oauth2Client(Customizer.withDefaults())
                .oauth2Login(customizer -> customizer.userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig.userAuthoritiesMapper(userAuthoritiesMapper())));
        return httpSecurity.build();
    }

    private GrantedAuthoritiesMapper userAuthoritiesMapper() {

        return authorities -> {
            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
            authorities.forEach(authority ->{
                if(authority instanceof OidcUserAuthority){
                    OidcUserAuthority oidcUserAuthority = (OidcUserAuthority) authority;
                    OidcIdToken oidcIdToken = oidcUserAuthority.getIdToken();
                    LOG.info("Username from ID token : {} ", oidcIdToken.getPreferredUsername());
                    OidcUserInfo userInfo = oidcUserAuthority.getUserInfo();
                    List<SimpleGrantedAuthority> groupAuthorities = userInfo.getClaimAsStringList(GROUPS_CLAIM)
                            .stream()
                            .map(group -> new SimpleGrantedAuthority(ROLE + group.toUpperCase()))
                            .toList();
                    mappedAuthorities.addAll(groupAuthorities);
                }
            });
            return mappedAuthorities;
        };

    }

}
