package com.bw.controller;

import com.bw.access.AccessLimit;
import com.bw.pojo.MiaoShaUser;
import com.bw.redis.GoodsKey;
import com.bw.redis.RedisService;
import com.bw.service.GoodsService;
import com.bw.util.CodeMsg;
import com.bw.util.Result;
import com.bw.vo.GoodsDetailVo;
import com.bw.vo.GoodsVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/goods")
public class GoodsController {
   /* @RequestMapping("/tolist")
    public String toList(@CookieValue(value = MiaoShaUserService.COOKIE_NAME_TOKEN,required = false)String cookieToken,
                         @RequestParam(value = MiaoShaUserService.COOKIE_NAME_TOKEN,required = false)String requestToken){
        if(StringUtils.isEmpty(cookieToken)&&StringUtils.isEmpty(requestToken)){
            return "login";
        }
        return "goods_list";
    }*/
    int i=1;
    @Autowired
    GoodsService goodsService;
    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;
    @Autowired
    RedisService redisService;

    //压力测试 未优化  5000*10     qps:1918     error%:61
    @AccessLimit(seconds = 5,maxCount = 5,needLogin = true)
    @RequestMapping(value = "tolist",produces="text/html")
    @ResponseBody
    public String tolist(MiaoShaUser miaoShaUser, Model model, HttpServletResponse response, HttpServletRequest request){
       if (miaoShaUser==null){
           return "请重新登录";
       }

       //3 从缓存里面取页面数据
       String html = redisService.get(GoodsKey.getGoodsListKey, "", String.class);
       //4判断如果有数据直接返回
       if(!StringUtils.isEmpty(html)){
            return html;
       }
        //5 如果缓存没有数据，从数据库 查询商品列表数据
       List<GoodsVo> list=goodsService.findGoods();
       model.addAttribute("goods",list);
       //6 把查询出来的数据渲染成html字符串
       WebContext ctx = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
       html = thymeleafViewResolver.getTemplateEngine().process("goods_list", ctx);

       //如果html不为空 把html保存到reids里面，注意保持时间为60秒
       if(!StringUtils.isEmpty(html)){
           redisService.set(GoodsKey.getGoodsListKey,"",html);
       }
        return html;
    }

    @RequestMapping(value = "/todetail/{goodsid}")
    @ResponseBody
    public Result<GoodsDetailVo> todetail(MiaoShaUser miaoShaUser,@PathVariable(value = "goodsid") Long id){
        if (miaoShaUser==null){
            return Result.error(CodeMsg.SESSION_TIMEOUT);
        }//看用户时是否登录

        GoodsVo goods=goodsService.findMiaoShaGoods(id);

        long start=goods.getStartDate().getTime();
        long end = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();
        long remainSeconds=0;//秒杀开的剩余时间
        long miaoshaStatus=0;// 0秒杀倒计时 1秒杀进行中 2秒杀已结束
        if(now<start){
            miaoshaStatus=0;//秒杀未开始
            remainSeconds=(start-now)/1000;
        }else if(now>end){
            miaoshaStatus=2;//秒杀结束
            remainSeconds=-1;
        }else{
            miaoshaStatus=1;//秒杀进行中
            remainSeconds=0;
        }
        GoodsDetailVo goodsDetailVo = new GoodsDetailVo(miaoshaStatus, remainSeconds, goods, miaoShaUser);
        return Result.success(goodsDetailVo);
    }

}
