package com.korit.board.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) //프로그램 실행 중에 어노테이션을 읽어들임
@Target(ElementType.METHOD)         //메소드 타입 대상
public @interface ValidAop {

}
