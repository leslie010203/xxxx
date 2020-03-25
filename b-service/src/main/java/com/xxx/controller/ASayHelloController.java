package com.xxx.controller;

import com.xxx.controller.feign.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/*
 * @Author 王川
 * @Date 2020/3/17 10:44
 */
@RestController
public class ASayHelloController {

    @Autowired
    private A a;


    /*
     * @ClassName ASayHelloController
     * @Desc TODO   读取配置文件中的端口
     * @Date 2020/3/17 10:44
     * @Version 1.0.0
     */
    @Value("${server.port}")
    private String port;

    /*
     * @ClassName ASayHelloController
     * @Desc TODO   Say Hello
     * @Date 2020/3/17 10:44
     * @Version 1.0
     */
    @RequestMapping("/hello")
    public String hello() {
        return "Hello！I'm a. port：" + port;
    }

    /*
     * @ClassName ASayHelloController
     * @Desc TODO   接收从网关传入的参数
     * @Date 2020/3/17 10:44
     * @Version 1.0
     */
    @RequestMapping("/name")
    public String name(String name) {
        return "My name is " + name + ". bbb";
    }

    /*
     * @ClassName ASayHelloController
     * @Desc TODO   接收从网关传入的参数
     * @Date 2020/3/17 10:44
     * @Version 1.0
     */
    @RequestMapping("/age")
    public String age(String age) {
        return "I am " + age + " years old this year. bbb";
    }

    /*
     * @ClassName ASayHelloController
     * @Desc TODO   接收从网关传入的参数
     * @Date 2020/3/17 10:44
     * @Version 1.0
     */
    @RequestMapping("/routeAll")
    public String routeAll(String pass) {
        return "Can I pass? " + pass + "! port：" + port;
    }


    @RequestMapping(value = "/apiOrder/xxxx",method = RequestMethod.GET)
    public String checkApiApplyTerm(@RequestParam("url") String url, @RequestParam("clientId") String clientId) {
        return a.checkApiApplyTerm(url, clientId);
    }

}