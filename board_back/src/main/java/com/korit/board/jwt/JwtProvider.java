package com.korit.board.jwt;

import com.korit.board.entity.User;
import com.korit.board.repository.UserMapper;
import com.korit.board.security.PrincipalUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {
    private final Key key;
    private final UserMapper userMapper;

    public JwtProvider(@Value("${jwt.secret}") String secret, @Autowired UserMapper userMapper) {
        key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.userMapper = userMapper;
    }

    public String generateToken(Authentication authentication) {
        //토큰에 넣어야 할 정보(공개되도 되는 정보) : id, 권한
        String email = authentication.getName();

        Date date = new Date(new Date().getTime() + (1000 * 60 * 60 *24) ); // 만료 날짜 시간 // 24시간

        //jwt 토큰 생성 후 리턴
        return Jwts.builder()
                .setSubject("AccessToken")      //토큰 이름
                .setExpiration(date)            //토큰 만료시간
                .claim("email", email)          //jwt 사용자 설정 키값
                .signWith(key, SignatureAlgorithm.HS256)        //암호화
                .compact();
    }

    public Claims getClaims(String token) {
        Claims claims = null;

        try {
            claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            System.out.println(e.getClass() + " : " + e.getMessage());
        }
        return claims;      //token이 유효하지 않으면 null이 리턴됨
    }

    public String getToken(String bearerToken) {    //token에 "Bearer " 문구 제거
        if(!StringUtils.hasText(bearerToken)) {
            return null;
        }
        return bearerToken.substring("Bearer ".length());
    }

    public Authentication getAuthentication(String token) {
        // jwt가 아닌 DB에서 정보를 가져와야 함, jwt에는 보안 위험성 때문에 사용자 정보를 잘 담아두지 않는 편임

        Claims claims = getClaims(token);   //Claims 해체
        if(claims == null) { //token이 유효하지 않은 경우 null
            return null;
        }

        User user = userMapper.findUserByEmail(claims.get("email").toString());
        if(user == null) {  //DB에서 user가 존재하지 않는 경우, DB에서 user를 삭제했을 경우 고려
            return null;
        }

        PrincipalUser principalUser = new PrincipalUser(user);
        return new UsernamePasswordAuthenticationToken(principalUser, null, principalUser.getAuthorities());
    }

    //이메일 인증을 위한 토큰 발행
    public String generateAuthMailToken(String email) {
        Date date = new Date(new Date().getTime() + 1000 * 60 * 5);     //인증 유효시간 5분 설정

        return Jwts.builder()
                .setSubject("AuthenticationEmailToken")
                .setExpiration(date)
                .claim("email", email)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
