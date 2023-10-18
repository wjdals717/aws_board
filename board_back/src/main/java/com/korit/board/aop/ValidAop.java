package com.korit.board.aop;

import com.korit.board.exception.ValidException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;

import java.util.HashMap;
import java.util.Map;

/**
 * AOP는 필터와 같은 역할을 한다.
 * */

@Aspect
@Component  //IoC에 등록
public class ValidAop {

//    @Pointcut("execution(* com.korit.board.controller.*.*(..))")
    @Pointcut("@annotation(com.korit.board.aop.annotation.ValidAop)")
    private void pointCut() { }

    @Around("pointCut()") /* 포인트컷 */
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        System.out.println("ValidAop 전처리");
        //Validation 체크 -> 후처리 필요없음, 전처리만 필요

        Object[] args = proceedingJoinPoint.getArgs();
        BeanPropertyBindingResult bindingResult = null;

//        for (Object arg: args) {
//            System.out.println(arg.getClass());
//        }

        for(Object arg: args) {
            if (arg.getClass() == BeanPropertyBindingResult.class) {
                bindingResult = (BeanPropertyBindingResult) arg;
                break;
            }
        }

        if(bindingResult == null) {     //nullpointerException 확인
            return proceedingJoinPoint.proceed();   //여기서 바로 return 하면 후처리는 없는 거임
        }

        if (bindingResult.hasErrors()){ //null이 아닐 경우 error 있는지 확인
            Map<String, String> errorMap = new HashMap<>();
            bindingResult.getFieldErrors().forEach(fieldError -> {
                errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
            });
            throw new ValidException(errorMap);
        }

        //.proceed() 메소드가 호출되기 전이 전처리
        Object target = proceedingJoinPoint.proceed();  // 메소드의 body
        System.out.println(target);
        //.proceed() 메소드가 리턴된 후가 후처리
        System.out.println("후처리");
        return target;
    }
}
