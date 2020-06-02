package com.bw.controller;

import com.bw.access.AccessLimit;
import com.bw.pojo.*;
import com.bw.rabbit.MQSender;
import com.bw.rabbit.MiaoshaMessage;
import com.bw.redis.*;
import com.bw.service.GoodsService;
import com.bw.service.MiaoShaService;
import com.bw.util.CodeMsg;
import com.bw.util.Result;
import com.bw.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/miaosha")
public class MiaoShaController implements InitializingBean {
    @Autowired
    GoodsService goodsService;
    @Autowired
    MiaoShaService miaoShaService;
    @Autowired
    RedisService redisService;
    @Autowired
    MQSender mqSender;

    Map<Long,Boolean> localOverMap = new HashMap<Long,Boolean>();

    //项目启动立刻执行
    @Override
    public void afterPropertiesSet() throws Exception {
        List<MiaoshaGoods> miaoshaGoods = goodsService.findAllMiaoShaGoods();
        for (MiaoshaGoods goods:miaoshaGoods){
            redisService.set(MiaoshaKey.getGoodsCountKey,""+goods.getId(),goods.getStockCount());
            localOverMap.put(goods.getId(),false);
        }
    }

    @RequestMapping(value = "reset",method = RequestMethod.GET)
    @ResponseBody
    public Result<Boolean>result(Model model){
        List<GoodsVo> goodsVos = goodsService.findGoods();
        for (GoodsVo goodsVo:goodsVos){
            goodsVo.setStockCount(10);
            redisService.set(MiaoshaKey.getGoodsCountKey,""+goodsVo.getId(),10);
            localOverMap.put(goodsVo.getId(),false);
        }
        redisService.delete(MiaoshaKey.getOrderKey);
        redisService.delete(MiaoshaKey.getIsGoodsOverKey);
        miaoShaService.reset(goodsVos);
        return Result.success(true);
    }


    //压力测试 未优化  5000*10     qps:1364     error%:57
    @RequestMapping(value = "/{path}/doMiaoSha",method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> doMiaoSha(MiaoShaUser miaoShaUser, Long goodsId,
                                     @PathVariable("path")String path){
        if (miaoShaUser==null){
            return Result.error(CodeMsg.SESSION_TIMEOUT);
        }

        String creatpPath = redisService.get(MiaoshaKey.getMiaoShaPath, ""+miaoShaUser.getId()+"_"+goodsId, String.class);
        if(!path.equals(creatpPath)){
            return Result.error(CodeMsg.MIAOSHAPATHERROR);
        }

        if(localOverMap.get(goodsId)){
            return Result.error(CodeMsg.MIAO_SHA_GOODS_NULL);
        }

        MiaoshaOrder miaoshaOrder=miaoShaService.findByMiaoShaUserIdAndByGoodsId(miaoShaUser.getId(),goodsId);
        if (miaoshaOrder!=null){
            return Result.error(CodeMsg.GOODS_ISREPEAT);
        }

        Long cacheStock = redisService.decr(MiaoshaKey.getGoodsCountKey, "" + goodsId);
        if(cacheStock<=-1){
            localOverMap.put(goodsId,true);
            return  Result.error(CodeMsg.MIAO_SHA_GOODS_NULL);
        }

        MiaoshaMessage miaoshaMessage = new MiaoshaMessage();
        miaoshaMessage.setGoodsId(goodsId);
        miaoshaMessage.setUser(miaoShaUser);

        mqSender.miaoshaSend(miaoshaMessage);

        return Result.success(0);


       /* GoodsVo goodsVo = goodsService.findMiaoShaGoods(goodsId);
        if(goodsVo.getStockCount()<=0){
            return Result.error(CodeMsg.MIAO_SHA_GOODS_NULL);
        }

        MiaoshaOrder miaoshaOrder=miaoShaService.findByMiaoShaUserIdAndByGoodsId(miaoShaUser.getId(),goodsId);
        if (miaoshaOrder!=null){
            return Result.error(CodeMsg.GOODS_ISREPEAT);
        }

        OrderInfo orderInfo=miaoShaService.AddMiaoShaOrder(miaoShaUser,goodsVo);
*/

    }

    @RequestMapping(value="/result",method=RequestMethod.GET)
    @ResponseBody
    public Result<Long> result(MiaoShaUser miaoshaUser ,@RequestParam(value="goodsId") long goodsId ) {
        //如果没有登录跳转到登录页面
        if(miaoshaUser==null) {
            return Result.error(CodeMsg.SESSION_TIMEOUT);
        }

        long result = this.miaoShaService.getMiaoshaResult(miaoshaUser.getId(),goodsId);

        return Result.success(result);
    }

    @AccessLimit(seconds = 10,maxCount = 5,needLogin = true)
    @RequestMapping(value = "path",method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getPath(MiaoShaUser miaoShaUser,Long goodsId,@RequestParam(value = "verifyCode",defaultValue = "0")int verifyCode,
            HttpServletRequest request){
        if(miaoShaUser==null){
            return Result.error(CodeMsg.SESSION_TIMEOUT);
        }

       /* String uri = request.getRequestURI();
        String key=uri+"_"+miaoShaUser.getId()+"_"+goodsId;

        Integer count = redisService.get(AccessKey.getAccessKey10, key, Integer.class);
        if(count==null){
            redisService.set(AccessKey.getAccessKey10,key,1);
        }else if(count<5){
            redisService.incr(AccessKey.getAccessKey10,key);
        }else{
            return  Result.error(CodeMsg.ACCESS_LIMIT);
        }
*/
        boolean b = miaoShaService.checkVerifyCode(miaoShaUser, goodsId, verifyCode);
        if(!b){
            return Result.error(CodeMsg.REQUEST_CODEERROR);
        }

        String path=miaoShaService.creatPath(miaoShaUser,goodsId);
        return Result.success(path);
    }


    @RequestMapping(value = "verifyCode" ,method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getMiaoShaVerifyCode(MiaoShaUser miaoShaUser, HttpServletResponse response,
                                               @RequestParam("goodsId")Long goodsId) throws IOException {
        if(miaoShaUser==null){
            return  Result.error(CodeMsg.SESSION_TIMEOUT);
        }
        try {
            BufferedImage image=miaoShaService.getMiaoShaVerifyCode(miaoShaUser,goodsId);
            OutputStream outputStream=response.getOutputStream();
            ImageIO.write(image,"JPEG",outputStream);
            outputStream.flush();
            outputStream.close();
            return null;
        }catch (Exception e){
            return Result.error(CodeMsg.MIAOSHA_FAIL);
        }

    }

}
