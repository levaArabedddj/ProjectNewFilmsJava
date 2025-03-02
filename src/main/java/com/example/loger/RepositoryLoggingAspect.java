package com.example.loger;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RepositoryLoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(RepositoryLoggingAspect.class);

    @AfterReturning(pointcut = "execution(* com.example.oopkursova.Repository.*.*(..))", returning = "result")
    public void logAnnotatedRepositoryMethods(Object result) {
        logger.info("Repository method executed successfully with result: {}", result);
    }
}

