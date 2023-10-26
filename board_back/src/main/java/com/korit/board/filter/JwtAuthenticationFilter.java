package com.korit.board.filter;

import com.korit.board.config.SecurityConfig;
import com.korit.board.jwt.JwtProvider;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilter {

    private final JwtProvider jwtProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        String bearerToken = httpServletRequest.getHeader("Authorization");
        System.out.println(bearerToken);
        String token = jwtProvider.getToken(bearerToken);
        Authentication authentication = jwtProvider.getAuthentication(token);
        System.out.println(authentication);

        if(authentication != null) {
            SecurityContextHolder.getContext().setAuthentication(authentication);   //setAuthentication에 null만 아니면 인증되었다고 봄
        }

        chain.doFilter(request, response);
    }
}
