package com.korit.board.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Aspect
@Component
public class TimeAop {
    @Pointcut("@annotation(com.korit.board.aop.annotation.TimeAop)")
    private void pointCut() {}

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        //메소드 실행 전에 타임이 시작되고 도착하면 타임 종료 -> 전처리/후처리 필요
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object target = proceedingJoinPoint.proceed();

        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeSeconds() + "초");

        return target;
    }
}
