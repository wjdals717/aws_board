package com.korit.board.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UpdatePasswordReqDto {
    @NotBlank
    private String oldPassword;
    @NotBlank
    private String NewPassword;
    @NotBlank
    private String checkNewPassword;
}
