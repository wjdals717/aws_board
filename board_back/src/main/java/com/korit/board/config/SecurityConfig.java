package com.korit.board.config;

import com.korit.board.security.PrincipalEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableWebSecurity  //기존 security가 아닌 사용자 설정 security를 IoC에 등록해서 사용하겠다.
@Configuration
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final PrincipalEntryPoint principalEntryPoint;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors();                        //WebMvcConfig의 CORS 설정을 적용
        http.csrf().disable();              //주로 SSR에서 사용 -> 사용 안 함
        http.authorizeRequests()            //요청에 대한 인증 처리
                .antMatchers("/auth/**")    //엔드포인트
                .permitAll()               //모두 허용
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(principalEntryPoint);
    }
}
