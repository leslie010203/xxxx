package com.xxx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Author 王川
 * @Date 2020/3/17 10:42
 **/

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients("com.xxx")
public class B {
    public static void main(String[] args) {
        SpringApplication.run(B.class, args);
    }
}
