package com.example.loger;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ServiceLoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(ServiceLoggingAspect.class);

    @AfterReturning(pointcut = "execution(* com.example.oopkursova.Service.*.*(..))", returning = "result")
    public void logAnnotatedServiceMethods(Object result) {
        logger.info("Service method executed successfully with result: {}", result);
    }
}

