package com.example.loger;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("execution(* com.example.oopkursova.Controllers.*.*(..))")
    public Object logControllerMethodsExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        // Викликаємо метод
        Object result = joinPoint.proceed();

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        logger.info("Method {} executed in {} ms", joinPoint.getSignature(), executionTime);
        return result;
    }

}
