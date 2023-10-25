package com.korit.board.config;

import com.korit.board.filter.JwtAuthenticationFilter;
import com.korit.board.security.PrincipalEntryPoint;
import com.korit.board.security.oauth2.OAuth2SuccessHandler;
import com.korit.board.service.PrincipalUserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity  //기존 security가 아닌 사용자 설정 security를 IoC에 등록해서 사용하겠다.
@Configuration
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final PrincipalEntryPoint principalEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final PrincipalUserDetailService principalUserDetailService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;        //component에 등록해뒀음

    @Bean
    public BCryptPasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors();                        //WebMvcConfig의 CORS 설정을 적용
        http.csrf().disable();              //주로 SSR에서 사용 -> 사용 안 함
        http.authorizeRequests()            //요청에 대한 인증 처리
                .antMatchers("/auth/**", "/board/**")    //엔드포인트
                .permitAll()                             //모두 허용
                .antMatchers("/board/content")
                .authenticated()
                .anyRequest()
                .authenticated()
                .and()
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(principalEntryPoint)
                .and()
                .oauth2Login()
                .loginPage("http://localhost:3000/auth/signin")
                .successHandler(oAuth2SuccessHandler)
                .userInfoEndpoint()                                 //컨트롤러 역할
                .userService(principalUserDetailService);           //서비스
    }
}
