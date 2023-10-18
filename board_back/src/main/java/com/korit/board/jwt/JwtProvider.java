package com.korit.board.jwt;

import com.korit.board.security.PrincipalUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {
    private final Key key;

    public JwtProvider(@Value("${jwt.secret}") String secret) {
        key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public String generateToken(Authentication authentication) {
        //토큰에 넣어야 할 정보 : id, 권한
        String email = authentication.getName();
        PrincipalUser principalUser = (PrincipalUser) authentication.getPrincipal();

        Date date = new Date(new Date().getTime() + (1000 * 60 * 60 *24) ); //만료 날짜 시간 // 24시간

        //jwt 토큰 생성 후 리턴
        return Jwts.builder()
                .setSubject("AccessToken")      //토큰 이름
                .setExpiration(date)            //토큰 만료시간
                .claim("email", email)          //jwt 사용자 설정 키값
                .claim("disabled", principalUser.isEnabled())   //이메일 인증 여부
                .signWith(key, SignatureAlgorithm.HS256)        //암호화
                .compact();
    }

    public Claims getClaims(String token) {
        Claims claims = null;

        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return claims;
    }
}
