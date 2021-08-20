package com.flextalk.we;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class FlextalkApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlextalkApplication.class, args);
    }
}
