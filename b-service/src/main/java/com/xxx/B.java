package com.xxx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Author 王川
 * @Date 2020/3/17 10:42
 **/

@SpringBootApplication
@EnableDiscoveryClient
public class B {
    public static void main(String[] args) {
        SpringApplication.run(B.class, args);
    }
}
