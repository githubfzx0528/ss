package com.bw.access;

import java.io.OutputStream;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bw.pojo.MiaoShaUser;
import com.bw.redis.AccessKey;
import com.bw.redis.RedisService;
import com.bw.service.MiaoShaService;
import com.bw.service.MiaoShaUserService;
import com.bw.util.CodeMsg;
import com.bw.util.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.fastjson.JSON;


@Service
public class AccessInterceptor  extends HandlerInterceptorAdapter{
	
	@Autowired
	MiaoShaUserService userService;
	
	@Autowired
	RedisService redisService;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		if(handler instanceof HandlerMethod) {
			MiaoShaUser user = getUser(request, response);
			 
			UserContext.setUser(user);//把数据放到线程里面
			HandlerMethod hm = (HandlerMethod)handler;
			AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
			if(accessLimit == null) {
				return true;
			}
			int seconds = accessLimit.seconds();
			int maxCount = accessLimit.maxCount();
			boolean needLogin = accessLimit.needLogin();
			String key = request.getRequestURI();
			if(needLogin) {
				if(user == null) {
					render(response, CodeMsg.SESSION_TIMEOUT);
					return false;
				}
				key += "_" + user.getId();
			}else {
				//do nothing
			}
			AccessKey ak = AccessKey.withExpire(seconds);
			Integer count = redisService.get(ak, key, Integer.class);
	    	if(count  == null) {
	    		 redisService.set(ak, key, 1);
	    	}else if(count < maxCount) {
	    		 redisService.incr(ak, key);
	    	}else {
	    		render(response, CodeMsg.ACCESS_LIMIT);
	    		return false;
	    	}
		}
		return true;
	}

	private void render(HttpServletResponse response, CodeMsg cm)throws Exception {
		response.setContentType("application/json;charset=UTF-8");
		OutputStream out = response.getOutputStream();
		String str  = JSON.toJSONString(Result.error(cm));
		out.write(str.getBytes("UTF-8"));
		out.flush();
		out.close();
	}

	private MiaoShaUser getUser(HttpServletRequest request, HttpServletResponse response) {
		String paramToken = request.getParameter(MiaoShaUserService.COOKIE_NAME_TOKEN);//获取后台的cooki
		String cookieToken = getCookieValue(request, MiaoShaUserService.COOKIE_NAME_TOKEN);//获取后台cooki去与浏览器的cooki进行比较
		if(StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
			return null;
		}
		String token = StringUtils.isEmpty(paramToken)?cookieToken:paramToken;
		return userService.getUserByToke(response,token);
	}
	
	private String getCookieValue(HttpServletRequest request, String cookiName) {//进行token值得比较
		Cookie[]  cookies = request.getCookies();//获取浏览器cookie的值
		if(cookies == null || cookies.length <= 0){//判断是否为空
			return null;
		}
		for(Cookie cookie : cookies) {//不为空时进行遍历转换成对象
			if(cookie.getName().equals(cookiName)) {//用浏览器的cookie中的值与后台产生的token进行对比
				return cookie.getValue();
			}
		}
		return null;
	}
	
}
