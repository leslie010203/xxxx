package com.xxx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @Author 王川
 * @Date 2020/3/17 10:29
 **/
@SpringBootApplication
@EnableEurekaServer
public class RegistryServer {
    public static void main(String[] args) {
        SpringApplication.run(RegistryServer.class, args);
    }
}
