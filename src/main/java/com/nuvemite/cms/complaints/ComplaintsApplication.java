package com.nuvemite.cms.complaints;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ComplaintsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ComplaintsApplication.class, args);
    }
}
