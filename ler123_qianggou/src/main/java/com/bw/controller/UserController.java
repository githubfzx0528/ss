package com.bw.controller;

import com.bw.pojo.MiaoShaUser;
import com.bw.util.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/user")
public class UserController {

	/**
	 * qps: 450  ,测试10000次访问
	 * @param miaoshaUser
	 * @return
	 */
   @RequestMapping("/userinfo")
   @ResponseBody
   public Result<MiaoShaUser> list(MiaoShaUser miaoshaUser ) {  //数据来自redis
	   System.out.println("**"+miaoshaUser);
	   return Result.success(miaoshaUser);
   }
  
	 	
}
