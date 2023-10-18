package com.korit.board.exception;

import lombok.Getter;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@Getter
public class DuplicatieException extends RuntimeException{
    private Map<String, String> errorMap;

    public DuplicatieException(Map<String, String> errorMap) {
        super("중복 검사 오류");
        this.errorMap = errorMap;
    }
}
