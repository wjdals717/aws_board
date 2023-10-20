package com.korit.board.controller;

import com.korit.board.aop.annotation.TimeAop;
import com.korit.board.exception.AuthMailException;
import com.korit.board.exception.DuplicatieException;
import com.korit.board.exception.MismatchPasswordException;
import com.korit.board.exception.ValidException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ExceptionControllerAdvice {

    @TimeAop
    @ExceptionHandler(ValidException.class)
    public ResponseEntity<?> validException(ValidException validException) {
        System.out.println("예외처리 됨!!!");
        return ResponseEntity.badRequest().body(validException.getErrorMap());
    }

    @ExceptionHandler(DuplicatieException.class)
    public ResponseEntity<?> duplicateException(DuplicatieException duplicatieException) {
        System.out.println("예외처리 됨!!!");
        return ResponseEntity.badRequest().body(duplicatieException.getErrorMap());
    }

    // email 불일치 예외처리
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<?> usernameNotFoundException(UsernameNotFoundException usernameNotFoundException) {
        Map<String, String> message = new HashMap<>();
        message.put("authError", "사용자 정보를 확인해주세요.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
    }

    // 비밀번호 불일치 예외처리
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> usernameNotFoundException(BadCredentialsException badCredentialsException) {
        Map<String, String> message = new HashMap<>();
        message.put("authError", "사용자 정보를 확인해주세요.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
    }
    
    // 이메일 미인증 예외처리
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<?> disabledException(DisabledException disabledException) {
        Map<String, String> message = new HashMap<>();
        message.put("disabled", "이메일 인증이 필요합니다.");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(message);
    }

    // jwt 토큰 예외처리
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<?> jwtException(JwtException jwtException) {
        Map<String, String> message = new HashMap<>();
        message.put("jwt", "인증이 유효하지 않습니다.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
    }

    @ExceptionHandler(AuthMailException.class)
    public ResponseEntity<?> mailException(AuthMailException mailException) {
        Map<String, String> message = new HashMap<>();
        message.put("authMail", mailException.getMessage());
        return ResponseEntity.ok().body(message);
    }

    @ExceptionHandler(MismatchPasswordException.class)
    public ResponseEntity<?> mismatchPasswordException(MismatchPasswordException mismatchPasswordException) {
        Map<String, String> message = new HashMap<>();
        message.put("mismatched", mismatchPasswordException.getMessage());
        return ResponseEntity.badRequest().body(message);
    }
}
