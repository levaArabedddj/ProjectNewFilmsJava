package com.example.oopkursova.loger;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @AfterReturning(pointcut = "execution(* com.example.oopkursova.Controllers.*.*(..))", returning = "result")
    public void logControllerMethods(Object result) {
        logger.info("Controller method executed successfully with result: {}", result);
    }

}
