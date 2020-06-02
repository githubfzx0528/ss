package com.bw.controller;

import com.bw.pojo.MiaoShaUser;
import com.bw.pojo.OrderInfo;
import com.bw.service.GoodsService;
import com.bw.service.OrderInfoService;
import com.bw.util.CodeMsg;
import com.bw.util.Result;
import com.bw.vo.GoodsVo;
import com.bw.vo.OrderDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("order")
public class OrederController {
    @Autowired
    OrderInfoService orderInfoService;
    @Autowired
    GoodsService goodsService;

    @RequestMapping("detail")
    @ResponseBody
    public Result<OrderDetailVo> detail(MiaoShaUser miaoShaUser,Long orderId){
        if (miaoShaUser==null) {
            return Result.error(CodeMsg.SESSION_TIMEOUT);
        }
        OrderInfo orderInfo=orderInfoService.findOrderInfo(orderId);
        if(orderId==null){
            return Result.error(CodeMsg.ORDER_NOT_FIND);
        }
        GoodsVo goods = goodsService.findMiaoShaGoods(orderInfo.getGoodsId());
        OrderDetailVo orderDetailVo = new OrderDetailVo();
        orderDetailVo.setGoods(goods);
        orderDetailVo.setOrder(orderInfo);
        return Result.success(orderDetailVo);
    }

}
