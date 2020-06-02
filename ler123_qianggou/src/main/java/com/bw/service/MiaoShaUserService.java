package com.bw.service;

import com.bw.dao.MiaoShaUserMapper;
import com.bw.pojo.MiaoShaUser;
import com.bw.redis.MiaoshaUserKey;
import com.bw.redis.RedisService;
import com.bw.redis.UserMiaoShaKey;
import com.bw.util.CodeMsg;
import com.bw.util.MD5Util;
import com.bw.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class MiaoShaUserService {
    public static final String COOKIE_NAME_TOKEN="token";
    @Autowired
    MiaoShaUserMapper miaoShaUserMapper;
    @Autowired
    RedisService redisService;

    public String doLogin(String mobile, String password, HttpServletResponse response) {
        MiaoShaUser miaoShaUser =getUserByMoblie(mobile);
        if(miaoShaUser==null){
            return CodeMsg.MOBILE_NOT_EXIST.getMsg();
        }
        String dbpassword= miaoShaUser.getPassword();
        String formPassToDBPass = MD5Util.formPassToDBPass(password, miaoShaUser.getSalt());
        if(!dbpassword.equals(formPassToDBPass)){
            return CodeMsg.PASSWORD_ERROR.getMsg();
        }
        String token= UUIDUtil.uuid();
        andCookieAndRedis(token,response,miaoShaUser);
        return token;
    }

    private MiaoShaUser getUserByMoblie(String mobile) {
        MiaoShaUser miaoShaUser = redisService.get(UserMiaoShaKey.get_tokenKey, mobile, MiaoShaUser.class);
        if(miaoShaUser!=null){
            return miaoShaUser;
        }
        MiaoShaUser user = miaoShaUserMapper.findUser(Long.parseLong(mobile));
        if(user!=null){
            redisService.set(UserMiaoShaKey.getUserByIdKey,"",user);
        }
        return user;
    }



    private void andCookieAndRedis(String token, HttpServletResponse response, MiaoShaUser miaoShaUser) {
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN,token);
        cookie.setMaxAge(MiaoshaUserKey.TOKEN_EXPIRE);
        cookie.setPath("/");//必须设置到跟目录
        response.addCookie(cookie);
        redisService.set(UserMiaoShaKey.get_tokenKey,token,miaoShaUser);
    }

    public MiaoShaUser getUserByToke(HttpServletResponse response, String token) {
        MiaoShaUser miaoShaUser=redisService.get(UserMiaoShaKey.get_tokenKey,token,MiaoShaUser.class);
        if(miaoShaUser!=null){
            andCookieAndRedis(token,response,miaoShaUser);
        }
        return miaoShaUser;
    }
}
