package com.example.oopkursova;

import com.example.oopkursova.config.JwtCore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
public class OopKursovaApplication {

    private JwtCore jwtCore;

    public void setJwtCore(JwtCore jwtCore) {
        this.jwtCore = jwtCore;
    }


    public static void main(String[] args) {
        SpringApplication.run(OopKursovaApplication.class, args);
    }

}
