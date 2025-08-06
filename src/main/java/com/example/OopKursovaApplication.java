package com.example;


import com.example.config.JwtCore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class OopKursovaApplication {

    private JwtCore jwtCore;

    public void setJwtCore(JwtCore jwtCore) {
        this.jwtCore = jwtCore;
    }


    public static void main(String[] args) {
        SpringApplication.run(OopKursovaApplication.class, args);
    }


}
