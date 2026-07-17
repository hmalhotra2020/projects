package com.example.imagic.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAgent {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(LoggingAgent.class);

    @Around("execution(* *(..)) && @annotation(com.example.imagic.aop.Monitored)")
    public Object log(ProceedingJoinPoint point) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = point.proceed();
        logger.info("className={},methodName={},timeMs={},threadId={}",
                MethodSignature.class.cast(point.getSignature()).getDeclaringType().getSimpleName(),
                MethodSignature.class.cast(point.getSignature()).getMethod().getName(),
                System.currentTimeMillis() - start,
                Thread.currentThread().getId()
        );
        return result;
    }

}
