package com.pragma.powerup.infrastructure.out.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class FeignClientInterceptor implements RequestInterceptor {

    @Value("${service.secret.plazoleta}")
    private String serviceSecret;

    private static final String SERVICE_SECRET_HEADER = "X-Service-Secret";

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_TOKEN_TYPE = "Bearer";

    @Override
    public void apply(RequestTemplate template) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // send secret key to microservices
        template.header(SERVICE_SECRET_HEADER, serviceSecret);

        if (authentication != null && authentication.isAuthenticated()) {
            Object credentials = authentication.getCredentials();

            if (credentials instanceof String) {
                template.header(AUTHORIZATION_HEADER, String.format("%s %s", BEARER_TOKEN_TYPE, credentials));
            }
        }
    }

}
