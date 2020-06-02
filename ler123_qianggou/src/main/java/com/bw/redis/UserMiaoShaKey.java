package com.bw.redis;

public class UserMiaoShaKey extends BasePrefix{

    private static int   tokenexpireSeconds=3600*24*2;

    public UserMiaoShaKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }
    public static UserMiaoShaKey get_tokenKey=new UserMiaoShaKey(tokenexpireSeconds,"tk");
    public static UserMiaoShaKey getUserByIdKey=new UserMiaoShaKey(0,"getUserByIdKey");
}
