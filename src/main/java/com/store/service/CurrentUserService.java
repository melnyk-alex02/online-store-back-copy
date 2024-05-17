package com.store.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.store.exception.DataNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CurrentUserService {
    public String getCurrentUserUuid() {
        return getCurrentUserUuidOptional().orElseThrow(() -> new DataNotFoundException("Current user not found"));
    }

    public String getCurrentUserEmail() {
        return getCurrentUserEmailOptional().orElseThrow(() -> new DataNotFoundException("Current user not found"));
    }

    public Optional<String> getCurrentUserUuidOptional() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(authentication -> {
                    if (authentication instanceof JwtAuthenticationToken token) {
                        return token.getName(); // Current user Uuid from Keycloak Token
                    } else if (authentication.getPrincipal() instanceof UserDetails userDetails) {
                        return userDetails.getUsername();
                    } else if (authentication.getPrincipal() instanceof String username) {
                        return username; // For JUnit
                    } else if (authentication.getPrincipal() instanceof DecodedJWT decodedJWT) {
                        return decodedJWT.getClaim("sub").asString();
                    }
                    return null; // When user is unauthenticated
                });
    }

    public Optional<String> getCurrentUserEmailOptional() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication()).map(authentication -> {
            if (authentication instanceof JwtAuthenticationToken token) {
                return token.getName();
            } else if (authentication.getPrincipal() instanceof DecodedJWT userDetails) {
                return userDetails.getClaim("email").asString();
            } else if (authentication.getPrincipal() instanceof String username) {
                return username; // For JUnit
            }
            return null; // When user is unauthenticated
        });
    }

    public List<String> getCurrentUserAuthorities() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(authentication -> authentication.getAuthorities().stream().map(Object::toString).toList())
                .orElse(List.of());
    }
}