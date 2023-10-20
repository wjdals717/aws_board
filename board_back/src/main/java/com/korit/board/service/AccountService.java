package com.korit.board.service;

import com.korit.board.entity.User;
import com.korit.board.exception.AuthMailException;
import com.korit.board.jwt.JwtProvider;
import com.korit.board.repository.UserMapper;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final UserMapper userMapper;        //이메일 인증 여부 업데이트
    private final JwtProvider jwtProvider;

    @Transactional(rollbackFor = Exception.class)   //update가 일어나므로 transactional
    public boolean authenticateMail(String token) {
        Claims claims = jwtProvider.getClaims(token);
        System.out.println(claims);

        if(claims == null) {        // null인 경우(토큰 만료, 위조 등), 토큰 사용 못함
            throw new AuthMailException("만료된 인증 요청입니다.");
        }
        String email = claims.get("email").toString();
        System.out.println(email);

        User user = userMapper.findUserByEmail(email);
        if(user.getEnabled() > 0) {   //이메일 인증이 된 경우
            throw new AuthMailException("이미 인증이 완료된 요청입니다.");
        }

        return userMapper.updateEnabledToEmail(email) > 0;
    }
}
