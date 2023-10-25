package com.korit.board.dto;

import com.korit.board.entity.User;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class MergeOauth2ReqDto {
    @NotBlank
    private String email;
    @NotBlank
    private String password;
    @NotBlank
    private String oauth2Id;
    @NotBlank
    private String provider;

    public User toUserEntity() {
        return User.builder()
                .email(email)           //이메일 찾아서
                .oauth2Id(oauth2Id)     //id, provider 업데이트 시킴
                .provider(provider)
                .build();
    }
}
