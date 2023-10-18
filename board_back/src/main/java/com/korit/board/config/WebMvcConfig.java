package com.korit.board.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //crossorigin 풀어줌
        registry.addMapping("/**")          //요청 엔드포인트
                .allowedMethods("*")        //요청 메소드
                .allowedOrigins("*");       //요청 서버
    }
}
