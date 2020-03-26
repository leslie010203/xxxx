package com.xxx.controller.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "dopwcc")
public interface A {

    @RequestMapping(value = "/apiOrder/xxxx/", method = RequestMethod.GET)
    String checkApiApplyTerm(@RequestParam("url") String url, @RequestParam("clientId") String clientId);

}
