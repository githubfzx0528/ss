package com.bw.controller;

import com.bw.pojo.User;
import com.bw.redis.RedisService;
import com.bw.service.TestService;
import com.bw.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class TestController {
    @Autowired
    TestService userService;
    @Autowired
    RedisService redisService;
    @GetMapping("a")
    public Result a(){
        List<User> list= new ArrayList<User>(userService.findAll());
        System.out.printf("集合"+list);
        return Result.success(list);
    }

    @GetMapping("b")
    public Result<Boolean> b(){
        Boolean flag=userService.insert();
        return Result.success(flag);
    }
    @GetMapping("c")
    public String c(Model model){
        model.addAttribute("name","张三");
        return "hello";
    }
}
