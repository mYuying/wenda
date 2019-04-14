package com.myy.wenda.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @RequestMapping("/fun1")
    public String test() {
        return "Hello , dear myy !";
    }

    @RequestMapping("/fun2")
    public String fun2(@RequestParam("name") String name) {
        return "I love " + name;
    }
}
