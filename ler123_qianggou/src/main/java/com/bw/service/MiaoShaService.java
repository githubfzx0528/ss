package com.bw.service;

import com.bw.dao.MiaoshaMapper;
import com.bw.pojo.MiaoShaUser;
import com.bw.pojo.MiaoshaGoods;
import com.bw.pojo.MiaoshaOrder;
import com.bw.pojo.OrderInfo;
import com.bw.redis.MiaoShaOrderKey;
import com.bw.redis.MiaoshaKey;
import com.bw.redis.RedisService;
import com.bw.util.UUIDUtil;
import com.bw.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
public class MiaoShaService {
    @Autowired
    MiaoshaMapper miaoshaMapper;
    @Autowired
    RedisService redisService;

    public MiaoshaOrder findByMiaoShaUserIdAndByGoodsId(Long userid, Long goodsid) {
        MiaoshaOrder miaoshaOrder = redisService.get(MiaoShaOrderKey.getMiaoShaOrderKey, ""+userid+goodsid, MiaoshaOrder.class);
        if(miaoshaOrder==null){
            miaoshaOrder= miaoshaMapper.findByMiaoShaoOrderByIdAndByGoodsId(userid,goodsid);
        }
        return miaoshaOrder;
    }

    @Transactional
    public OrderInfo AddMiaoShaOrder(MiaoShaUser miaoShaUser, GoodsVo goodsVo) {
        OrderInfo orderInfo=null;
        int i=miaoshaMapper.reduceStock(goodsVo.getGoodsId());
        if(i>0) {
            orderInfo = createOrder(miaoShaUser, goodsVo);
        }else{
            setGoodsOver(goodsVo.getId());
        }
        return orderInfo;
    }

    @Transactional
    public OrderInfo createOrder(MiaoShaUser miaoShaUser,GoodsVo goodsVo){
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setGoodsId(goodsVo.getId());
        orderInfo.setGoodsName(goodsVo.getGoodsName());
        orderInfo.setGoodsCount(1);//商品数量
        orderInfo.setDeliveryAddrId(0L);//收获地址id',
        orderInfo.setGoodsPrice(goodsVo.getGoodsPrice());
        orderInfo.setGoodsChannel(1);//1.pc 2.android 3.ios'
        orderInfo.setStatus(0);
        orderInfo.setUserId(miaoShaUser.getId());
        miaoshaMapper.insertOrderInfo(orderInfo);

        MiaoshaOrder miaoshaOrder = new MiaoshaOrder();
        miaoshaOrder.setGoodsId(goodsVo.getId());
        miaoshaOrder.setOrderId(orderInfo.getId());
        miaoshaOrder.setUserId(miaoShaUser.getId());
        miaoshaMapper.insertMiaoShaOrder(miaoshaOrder);

        redisService.set(MiaoshaKey.getOrderKey,""+miaoshaOrder.getUserId()+miaoshaOrder.getGoodsId(),miaoshaOrder);
        return orderInfo;
    }

    public void reset(List<GoodsVo> goodsVos) {
        for (GoodsVo goodsVo:goodsVos){
            MiaoshaGoods miaoshaGoods = new MiaoshaGoods();
            miaoshaGoods.setGoodsId(goodsVo.getId());
            miaoshaGoods.setStockCount(goodsVo.getStockCount());
            miaoshaMapper.restStock(miaoshaGoods);
        }
        miaoshaMapper.deleteOrders();
        miaoshaMapper.deleteMiaoshaOrders();
    }

    public long getMiaoshaResult(Long userid, long goodsId) {
        MiaoshaOrder order = miaoshaMapper.findByMiaoShaoOrderByIdAndByGoodsId(userid, goodsId);
        //三种情况
        if(order!=null) {
            return order.getOrderId();  //秒杀成功
        }else {
            Boolean isOver = getGoodsOver(goodsId);

            if(isOver) {  //秒杀失败  true  ,说明订单已经存在
                return -1;
            }else {  //
                return  0; //继续轮询
            }
        }
    }

    private void setGoodsOver(Long goodsId) {
        this.redisService.set(MiaoshaKey.getIsGoodsOverKey,""+goodsId, true);  //true 是失败

    }

    private Boolean getGoodsOver(Long goodsId) {
        return this.redisService.exists(MiaoshaKey.getIsGoodsOverKey,""+goodsId);

    }

    public String creatPath(MiaoShaUser miaoShaUser, Long goodsId) {
        String path = UUIDUtil.uuid();
        redisService.set(MiaoshaKey.getMiaoShaPath,""+miaoShaUser.getId()+"_"+goodsId ,path);
        return path;
    }


    //生成验证码--------------------------------------------------
    public BufferedImage getMiaoShaVerifyCode(MiaoShaUser miaoShaUser, Long goodsId) {

        if(miaoShaUser == null || goodsId <=0) {
            return null;
        }
        int width = 90;
        int height = 32;
        //create the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // set the background color
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        // draw the border
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);
        // create a random instance to generate the codes
        Random rdm = new Random();
        // make some confusion
        for (int i = 0; i < 50; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        // generate a random code
        String verifyCode = generateVerifyCode(rdm);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(verifyCode, 8, 24);
        g.dispose();
        //把验证码存到redis中
        int rnd = calc(verifyCode);
        redisService.set(MiaoshaKey.getMiaoshaVerifyCode, miaoShaUser.getId()+","+goodsId, rnd);
        //输出图片
        return image;
    }

    public boolean checkVerifyCode(MiaoShaUser user, long goodsId, int verifyCode) {
        if(user == null || goodsId <=0) {
            return false;
        }
        Integer codeOld = redisService.get(MiaoshaKey.getMiaoshaVerifyCode, user.getId()+","+goodsId, Integer.class);
        if(codeOld == null || codeOld - verifyCode != 0 ) {
            return false;
        }
        redisService.delete(MiaoshaKey.getMiaoshaVerifyCode, user.getId()+","+goodsId);
        return true;
    }

    private static int calc(String exp) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            return (Integer)engine.eval(exp);
        }catch(Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static char[] ops = new char[] {'+', '-', '*'};
    /**
     * + - *
     * */
    private String generateVerifyCode(Random rdm) {
        int num1 = rdm.nextInt(10);
        int num2 = rdm.nextInt(10);
        int num3 = rdm.nextInt(10);
        int num4 = rdm.nextInt(10);
        char op1 = ops[rdm.nextInt(3)];
        char op2 = ops[rdm.nextInt(3)];
        char op3 = ops[rdm.nextInt(3)];
        String exp = ""+ num1 + op1 + num2;
        return exp;
    }
}
