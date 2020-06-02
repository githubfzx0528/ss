package com.bw.redis;

public class MiaoshaKey extends BasePrefix{




    public MiaoshaKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static MiaoshaKey getOrderKey = new MiaoshaKey(0, "OrderKey");
    public static MiaoshaKey getIsGoodsOverKey = new MiaoshaKey(0, "GoodsOverKey");
    public static MiaoshaKey getGoodsCountKey =new MiaoshaKey(0,"GoodsCountKey");
    public static MiaoshaKey getMiaoShaPath=new MiaoshaKey(10,"MiaoShaPath");
    public static MiaoshaKey getMiaoshaVerifyCode=new MiaoshaKey(60,"MiaoshaVerifyCode");


}
