package com.store.webconfig;


import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidParameterException;
import java.security.interfaces.RSAPublicKey;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtValidator {
    private static final Logger logger = LoggerFactory.getLogger(JwtValidator.class);
    private final JwtDecoder jwtDecoder;
    private static final List<String> allowedIssuers = Collections.singletonList("https://test-happytails-security.lav.net.ua/realms/happytails");
    private final Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter;


    private String getKeycloakCertificateUrl(DecodedJWT token) {
        return token.getIssuer() + "/protocol/openid-connect/certs";
    }

    private RSAPublicKey loadPublicKey(DecodedJWT token) throws MalformedURLException, JwkException {
        final String url = getKeycloakCertificateUrl(token);
        JwkProvider provider = new UrlJwkProvider(new URL(url));

        return (RSAPublicKey) provider.get(token.getKeyId()).getPublicKey();
    }

    public Optional<Authentication> validateAndGetAuthentication(String token) {
        try {
            final DecodedJWT jwt = JWT.decode(token);

            if (!allowedIssuers.contains(jwt.getIssuer())) {
                throw new InvalidParameterException(String.format("Unknown Issuer %s", jwt.getIssuer()));
            }

            RSAPublicKey publicKey = loadPublicKey(jwt);
            Algorithm algorithm = Algorithm.RSA256(publicKey, null);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(jwt.getIssuer())
                    .build();

            Jwt parderJwt = jwtDecoder.decode(token);

            DecodedJWT decodedJWT = verifier.verify(token);

            Collection<GrantedAuthority> authorities = jwtGrantedAuthoritiesConverter.convert(parderJwt);
            Authentication authentication = new UsernamePasswordAuthenticationToken(decodedJWT, null, authorities);
            return Optional.of(authentication);
        } catch (Exception e) {
            logger.error("Failed to validate JWT", e);
            return Optional.empty();
        }
    }
}