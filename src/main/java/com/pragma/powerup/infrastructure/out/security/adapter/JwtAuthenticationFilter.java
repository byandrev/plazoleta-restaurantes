package com.pragma.powerup.infrastructure.out.security.adapter;

import com.pragma.powerup.infrastructure.out.security.models.CustomUserDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenValidator jwtTokenValidator;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = extractToken(request);

        if  (token == null || !jwtTokenValidator.verifyToken(token)) {
            filterChain.doFilter(request, response);
            return;
        }


        String username = jwtTokenValidator.getUsername(token);
        String rol = jwtTokenValidator.getClaim(token, "rol");
        String userId = jwtTokenValidator.getClaim(token, "id");

        if (userId == null || rol == null) {
            filterChain.doFilter(request, response);
            return;
        }

        CustomUserDetail userDetails = new CustomUserDetail(
                Long.parseLong(userId),
                username,
                null,
                getAuthorities(rol)
        );

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, token, getAuthorities(rol));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String tokenHeader = request.getHeader("Authorization");

        if (tokenHeader == null || !tokenHeader.startsWith("Bearer ")) {
            return null;
        }

        return tokenHeader.split(" ")[1].trim();
    }

    private Collection<SimpleGrantedAuthority> getAuthorities(String rol) {
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(rol));
        return authorities;
    }
}
