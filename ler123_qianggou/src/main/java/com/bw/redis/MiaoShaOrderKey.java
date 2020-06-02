package com.bw.redis;

public class MiaoShaOrderKey extends BasePrefix{
    public MiaoShaOrderKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static MiaoShaOrderKey getMiaoShaOrderKey =new MiaoShaOrderKey(0,"getMiaoShaOrderKey");
}
