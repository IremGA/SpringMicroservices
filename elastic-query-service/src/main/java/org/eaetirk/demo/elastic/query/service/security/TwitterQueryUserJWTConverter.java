package org.eaetirk.demo.elastic.query.service.security;

import org.eaetirk.demo.elastic.query.service.constant.Constants;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;
import java.util.stream.Collectors;

public class TwitterQueryUserJWTConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private static final String REALM_ACCESS_CLAIM = "realm_access";
    private static final String ROLES_CLAIM = "roles";
    private static final String SCOPE_CLAIM = "scope";
    private static final String USERNAME_CLAIM = "preferred_username";
    private static final String DEFAULT_ROLE_PREFIX = "ROLE_";
    private static final String DEFAULT_SCOPE_PREFIX = "SCOPE_";
    private static final String SCOPE_SEPARATOR = " ";

    private final TwitterQueryUserDetailsService twitterQueryUserDetailsService;

    public TwitterQueryUserJWTConverter(TwitterQueryUserDetailsService twitterQueryUserDetailsService) {
        this.twitterQueryUserDetailsService = twitterQueryUserDetailsService;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt source) {
        Collection<GrantedAuthority> authorities = getAuthoritiesFromJwt(source);
        return Optional.ofNullable(twitterQueryUserDetailsService
                .loadUserByUsername(source.getClaimAsString(USERNAME_CLAIM)))
                .map(userDetails -> {
                    ((TwitterQueryUser) userDetails).setAuthorities(authorities);
                    return new UsernamePasswordAuthenticationToken(userDetails, Constants.NA, authorities);
                }).orElseThrow(()->new BadCredentialsException("User couldn't be found"));
    }

    private Collection<GrantedAuthority> getAuthoritiesFromJwt(Jwt source){
        return getCombinedAuthorities(source)
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    private Collection<String> getCombinedAuthorities(Jwt source){
        Collection<String> authorities = getRoles(source);
        authorities.addAll(getScopes(source));
        return authorities;
    }
    @SuppressWarnings("unchecked")
    private Collection<String> getRoles(Jwt source){
        Map<String, Object> access_claim_map = (Map<String, Object>) source.getClaims().get(REALM_ACCESS_CLAIM);

        if(access_claim_map == null || access_claim_map.isEmpty()) {
            return Collections.emptyList();
        }
        Object roles = access_claim_map.get(ROLES_CLAIM);
        if (roles instanceof Collection<?>) {
            return Collections.synchronizedCollection((Collection<String>) roles)
                    .stream()
                    .map(auth -> DEFAULT_ROLE_PREFIX.concat(auth.toUpperCase()))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    private Collection<String> getScopes(Jwt source){
        Map<String, Object> access_claim_map = (Map<String, Object>) source.getClaims().get(REALM_ACCESS_CLAIM);

        if(access_claim_map == null || access_claim_map.isEmpty()) {
            return Collections.emptyList();
        }
        Object scopes = access_claim_map.get(ROLES_CLAIM);
        if (scopes instanceof String) {
            return Arrays.stream(((String) scopes).split(SCOPE_SEPARATOR))
                    .map(auth -> DEFAULT_SCOPE_PREFIX.concat(auth.toUpperCase()))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }


}

