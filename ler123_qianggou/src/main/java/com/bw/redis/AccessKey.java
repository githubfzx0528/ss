package com.bw.redis;

public class AccessKey extends BasePrefix{

    public AccessKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }
    public static AccessKey getAccessKey5=new AccessKey(10,"AccessKey5");
    public static AccessKey getAccessKey10=new AccessKey(10,"AccessKey10");
    public static AccessKey getAccessKey15=new AccessKey(10,"AccessKey15");


    public static AccessKey withExpire(int expireSeconds) {
        return new AccessKey(expireSeconds,"access");
    }
}
