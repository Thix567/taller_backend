package com.taller.mecanica;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class MecanicaApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("America/Lima"));
        SpringApplication.run(MecanicaApplication.class, args);
    }
}
