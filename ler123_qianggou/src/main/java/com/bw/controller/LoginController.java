package com.bw.controller;

import com.bw.service.MiaoShaUserService;
import com.bw.util.CodeMsg;
import com.bw.util.Result;
import com.bw.util.ValidatorUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("login")
public class LoginController {
    @Autowired
    MiaoShaUserService miaoShaUserService;

    @RequestMapping("/tologin")
    public String toLogin(){
        return "login";
    }

    @RequestMapping("dologin")
    @ResponseBody
    public Result<String> doLogin(String mobile,String password,HttpServletResponse response){
        if(StringUtils.isEmpty(password)){
            return Result.error(CodeMsg.PASSWORD_EMPTY);
        }
        if(StringUtils.isEmpty(mobile)){
            return Result.error(CodeMsg.MOBILE_EMPTY);
        }
        if(!ValidatorUtil.isMobile(mobile)) {
            return Result.error(CodeMsg.MOBILE_ERROR);
        }
        String  token = miaoShaUserService.doLogin(mobile,password,response);
     /*   if (msg.getCode()==0){
            return Result.success(token);
        }else{
            return Result.error(msg);
        }*/
        return Result.success(token);
    }
}
