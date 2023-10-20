package com.korit.board.security;

import com.korit.board.service.PrincipalUserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PrincipalProvider implements AuthenticationProvider {
    // password 검사 후 enabled 검사하도록 로직 구성
    // authenticationManagerBuilder 역할

    private final PrincipalUserDetailService principalUserDetailService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String password = (String) authentication.getCredentials();

        UserDetails principalUser = principalUserDetailService.loadUserByUsername(email);

        if(!passwordEncoder.matches(password, principalUser.getPassword())) {    //암호화 되지 않은 password와 DB에 저장된 암호화된 password를 비교해줌
            throw new BadCredentialsException("BadCredentials");
        }

        return new UsernamePasswordAuthenticationToken(principalUser, password, principalUser.getAuthorities());    //password : 암호화되지 않은 password
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return false;
    }
}
