package com.store.webconfig;

import com.store.exception.InvalidDataException;
import com.store.utils.CookieUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AccessTokenFilter extends OncePerRequestFilter {
    public final Logger logger = LoggerFactory.getLogger(AccessTokenFilter.class);
    public static final String ACCESS_TOKEN = "access_token";
    private static final String HEADER_NAME = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private final JwtValidator jwtValidator;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) {
        var authHeader = request.getHeader(HEADER_NAME);
        try {
            if (!StringUtils.isEmpty(authHeader) && StringUtils.startsWith(authHeader, BEARER_PREFIX)) {
                var tokenFromHeader = authHeader.substring(BEARER_PREFIX.length());
                Optional<Authentication> authentication = jwtValidator.validateAndGetAuthentication(tokenFromHeader);
                authentication.ifPresent(SecurityContextHolder.getContext()::setAuthentication);
            } else {
                var tokenFromCookies = CookieUtils.extractCookie(request.getCookies(), ACCESS_TOKEN);
                if (tokenFromCookies != null && jwtValidator.validateAndGetAuthentication(tokenFromCookies).isPresent()) {
                    Authentication usernamePasswordAuthenticationToken = jwtValidator.validateAndGetAuthentication(tokenFromCookies).get();
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }
            filterChain.doFilter(request, response);
        } catch (JwtException e) {
            logger.error("JWT validation failed: {}", e.getMessage());
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            throw new RuntimeException(e);
        } catch (InvalidDataException e) {
            logger.error("Invalid data: {}", e.getMessage());
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            throw new RuntimeException(e);
        } catch (ConstraintViolationException e) {
            logger.error("Constraint violation: {}", e.getCause().getMessage());
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            throw new RuntimeException(e);
        } catch (IOException | ServletException e) {
            logger.error("Error occurred: {}", e.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            throw new RuntimeException(e);
        }
    }
}