package com.persou.prontosus;

import org.springframework.boot.SpringApplication;

public class TestProntosusApplication {

    public static void main(String[] args) {
        SpringApplication.from(ProntosusApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
