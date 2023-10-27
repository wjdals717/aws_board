package com.korit.board.entity;

import com.korit.board.dto.PrincipalRespDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Data
public class User {
    private int userId;
    private String email;
    private String password;
    private String name;
    private String nickname;
    private int enabled;      //이메일 인증 여부 // 1 or 0
    private String profileUrl;
    private String oauth2Id;
    private String provider;
    private int userPoint;

    public PrincipalRespDto toPrincipalDto() {
        return PrincipalRespDto.builder()
                .userId(userId)
                .email(email)
                .name(name)
                .nickname(nickname)
                .enabled(enabled > 0)
                .profileUrl(profileUrl)
                .oauth2Id(oauth2Id)
                .provider(provider)
                .userPoint(userPoint)
                .build();
    }
}
