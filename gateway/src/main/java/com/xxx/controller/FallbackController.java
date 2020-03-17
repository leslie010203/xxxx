package com.xxx.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName FallbackController
 * @Desc TODO   网关断路器
 * @Date 2020/3/17 11:21
 * @Version 1.0
 */
@RestController
@RequestMapping("/")
public class FallbackController {
    /*
     * @ClassName FallbackController
     * @Desc TODO   网关断路器
     * @Date 2020/3/17 11:21
     * @Version 1.0
     */
    @RequestMapping("/fallback")
    public String fallback() {
        return "I'm Spring Cloud Gateway fallback.";
    }
}
