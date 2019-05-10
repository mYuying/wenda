package com.myy.wenda.controller;

import com.myy.wenda.util.JedisAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test1")
public class TestController1 {

    @Autowired
    JedisAdapter jedisAdapter;

    @RequestMapping("/fun1")
    public long add() {
        System.out.println("enter---");
        return jedisAdapter.sadd("a", "123");
    }
}
