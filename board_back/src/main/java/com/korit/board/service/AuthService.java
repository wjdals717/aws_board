package com.korit.board.service;

import com.korit.board.dto.MergeOauth2ReqDto;
import com.korit.board.dto.SigninReqDto;
import com.korit.board.dto.SignupReqDto;
import com.korit.board.entity.User;
import com.korit.board.exception.DuplicatieException;
import com.korit.board.exception.MismatchPasswordException;
import com.korit.board.jwt.JwtProvider;
import com.korit.board.repository.UserMapper;
import com.korit.board.security.PrincipalProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final PrincipalProvider principalProvider;
    private final JwtProvider jwtProvider;

    public boolean signup(SignupReqDto signupReqDto) {
        User user = signupReqDto.toUserEntity(passwordEncoder);

        //중복 검사
        int errorCode = userMapper.checkDuplicate(user);
        if(errorCode > 0) { // 0보다 클 경우 에러가 존재함
            responseDuplicateError(errorCode);
        }

        return userMapper.saveUser(user)> 0;
    }

    private void responseDuplicateError(int errorCode){     //중복검사 오류 메세지 출력 함수
        Map<String, String> errorMap = new HashMap<>();
        switch (errorCode) {
            case 1:
                errorMap.put("email", "이미 사용 중인 이메일입니다.");
                break;
            case 2:
                errorMap.put("nickname", "이미 사용 중인 닉네임입니다.");
                break;
            case 3:
                errorMap.put("email", "이미 사용 중인 이메일입니다.");
                errorMap.put("nickname", "이미 사용 중인 닉네임입니다.");
                break;
        }
        throw new DuplicatieException(errorMap);  //케이스에 따라 errorMap을 무조건 날림 // 이 함수 실행 조건이 error가 발생했다는 의미
    }

    public String signin(SigninReqDto signinReqDto) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(signinReqDto.getEmail(), signinReqDto.getPassword());

        Authentication authentication = principalProvider.authenticate(authenticationToken);        //로그인 정보가 DB에 있는 사용자 정보 중 일치하는 것이 있는지 확인

        return jwtProvider.generateToken(authentication);   //로그인 된 authentication을 넘겨주고 jwt Token 생성해서 리턴함
    }

    public boolean authenticate(String token) {
        Claims claims = jwtProvider.getClaims(token);
        if( claims == null ){       //null이면 토큰을 못 쓰는 경우 -> 예외 처리
            throw new JwtException("인증 토큰 유효성 검사 실패");
        }
        return Boolean.parseBoolean(claims.get("enabled").toString());
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean mergeOauth2(MergeOauth2ReqDto mergeOauth2ReqDto) {
        User user = userMapper.findUserByEmail(mergeOauth2ReqDto.getEmail());

        if(!passwordEncoder.matches(mergeOauth2ReqDto.getPassword(), user.getPassword())) {  //비밀번호가 서로 일치하지 않을 때
            throw new BadCredentialsException("BadCredentials");
        }

        return userMapper.updateOauth2IdAndProvider(mergeOauth2ReqDto.toUserEntity()) > 0;
    }
}
