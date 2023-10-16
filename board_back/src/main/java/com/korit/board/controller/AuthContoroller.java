package com.korit.board.controller;

import com.korit.board.aop.annotation.ArgsAop;
import com.korit.board.aop.annotation.ReturnAop;
import com.korit.board.aop.annotation.TimeAop;
import com.korit.board.aop.annotation.ValidAop;
import com.korit.board.dto.SignupReqDto;
import com.korit.board.exception.ValidException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthContoroller {
    @ReturnAop
    @ArgsAop
    @TimeAop
    @ValidAop   //이 어노테이션이 달린 메소드들은 ValidAop 실행됨!
    @CrossOrigin
    @PostMapping("/auth/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupReqDto signupReqDto, BindingResult bindingResult) {

        System.out.println("AuthController!!!");
        return ResponseEntity.ok(true);
    }
}
