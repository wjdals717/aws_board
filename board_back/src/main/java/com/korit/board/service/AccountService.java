package com.korit.board.service;

import com.korit.board.dto.UpdatePasswordReqDto;
import com.korit.board.dto.UpdateProfileImgReqDto;
import com.korit.board.entity.User;
import com.korit.board.exception.AuthMailException;
import com.korit.board.exception.MismatchPasswordException;
import com.korit.board.jwt.JwtProvider;
import com.korit.board.repository.UserMapper;
import com.korit.board.security.PrincipalUser;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final UserMapper userMapper;        //이메일 인증 여부 업데이트
    private final JwtProvider jwtProvider;
    private final BCryptPasswordEncoder passwordEncoder;

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

    @Transactional(rollbackFor = Exception.class)
    public boolean updateProfileImg(UpdateProfileImgReqDto updateProfileImgReqDto){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return userMapper.updateProfileUrl(User.builder()
                        .email(email)
                        .profileUrl(updateProfileImgReqDto.getProfileUrl())
                        .build()) > 0;
    }

    // 비밀번호를 바꾸기 전 비밀번호 확인
    public boolean updatePassword(UpdatePasswordReqDto updatePasswordReqDto) {
        PrincipalUser principalUser = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = principalUser.getUser();    //DB에 있는 비밀번호

        if(!passwordEncoder.matches(updatePasswordReqDto.getOldPassword(), user.getPassword())) {    //비밀번호 일치하지 않으면 예외 처리
            throw new BadCredentialsException("BadCredentials");
        }

        //일치하면 바꿀 비밀번호 확인 절차
        if(!Objects.equals(updatePasswordReqDto.getNewPassword(), updatePasswordReqDto.getCheckNewPassword())) {
            throw new MismatchPasswordException();
        }

        user.setPassword(passwordEncoder.encode(updatePasswordReqDto.getNewPassword()));    //새 비밀번호 암호화

        return userMapper.updatePassword(user) > 0;
    }
}
