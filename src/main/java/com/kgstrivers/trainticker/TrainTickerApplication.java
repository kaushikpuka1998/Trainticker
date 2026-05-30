package com.kgstrivers.trainticker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class TrainTickerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrainTickerApplication.class, args);
    }

}
