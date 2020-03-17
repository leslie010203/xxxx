package com.xxx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Author 王川
 * @Date 2020/3/17 10:41
 **/
@SpringBootApplication
@EnableDiscoveryClient
public class A {
    public static void main(String[] args) {
        SpringApplication.run(A.class, args);
    }
}
