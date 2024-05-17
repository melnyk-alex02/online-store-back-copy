package com.store.webconfig;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    public final Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter;
    public final AccessTokenFilter accessTokenFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .requiresChannel(channelConfigurer ->
                        channelConfigurer
                                .requestMatchers(r -> r.getHeader("X-Forwarded-Proto") != null)
                                .requiresSecure()
                ).cors(corsConfigurer -> {
                    CorsConfiguration corsConfig = new CorsConfiguration();
                    corsConfig.addAllowedOrigin("http://localhost:3000");
                    corsConfig.addAllowedOrigin("https://localhost:3000");
                    corsConfig.addAllowedOrigin("https://happy-tails-mantine.vercel.app");
                    corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
                    corsConfig.setAllowCredentials(true);
                    corsConfig.addAllowedHeader("*");
                    corsConfigurer.configurationSource(request -> corsConfig);
                })
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/api/users/register").permitAll()
                        .requestMatchers("/api/users/token").permitAll()
                        .requestMatchers("/api/login/oauth2/code/google").permitAll()
                        .requestMatchers("/api/login").permitAll()
                        .requestMatchers("/api/users/refresh-token").permitAll()
                        .requestMatchers("api/users/reset-password").permitAll()
                        .requestMatchers("api/users/reset-password/verify").permitAll()
                        .requestMatchers("/api/users/send-otp").permitAll()
                        .requestMatchers("/api/users/authenticate-otp").permitAll()
                        .requestMatchers(HttpMethod.GET).permitAll()
                        .requestMatchers("/api/posts/**").permitAll()
                        .requestMatchers("/api/cart/**").permitAll()
                        .requestMatchers("api/feedback/**").permitAll()
                        .requestMatchers("/api/orders").permitAll()
                        .requestMatchers("/error").permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))
                .oauth2ResourceServer(resourceServerConfigurer -> resourceServerConfigurer
                        .jwt(jwtConfigurer -> jwtConfigurer
                                .jwtAuthenticationConverter(jwtAuthenticationConverter))
                )
                .addFilterBefore(accessTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}