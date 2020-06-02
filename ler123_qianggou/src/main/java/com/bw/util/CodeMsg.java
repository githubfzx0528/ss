package com.bw.util;

public class CodeMsg {



    private int code;
	private String msg;
	
	//通用异常
	public static CodeMsg SUCCESS = new CodeMsg(0, "success");
	public static CodeMsg SERVER_ERROR = new CodeMsg(500100, "服务端异常");
	//登录模块 5002XX
	public static CodeMsg SESSION_ERROR = new CodeMsg(500210, "Session不存在或者已经失效");
	public static CodeMsg PASSWORD_EMPTY = new CodeMsg(500211, "登录密码不能为空");
	public static CodeMsg MOBILE_EMPTY = new CodeMsg(500212, "手机号不能为空");
	public static CodeMsg MOBILE_ERROR = new CodeMsg(500213, "手机号格式错误");
	public static CodeMsg MOBILE_NOT_EXIST = new CodeMsg(500214, "手机号不存在");
	public static CodeMsg PASSWORD_ERROR = new CodeMsg(500215, "密码错误");
	public static CodeMsg SESSION_TIMEOUT = new CodeMsg(500216, "登录信息过期");
	//商品模块 5003XX
	public static final CodeMsg MIAO_SHA_GOODS_NULL = new CodeMsg(500301,"此商品已售空");
	//订单模块 5004XX
	public static CodeMsg ORDER_NOT_FIND = new CodeMsg(500401, "订单不存在");
	//秒杀模块 5005XX
	public static final CodeMsg GOODS_ISREPEAT = new CodeMsg(500501,"该商品已秒杀过了") ;
	public static final CodeMsg MIAOSHAPATHERROR = new CodeMsg(500502,"请求地址不合法");
	public static final CodeMsg REQUEST_CODEERROR = new CodeMsg(500503,"验证码错误");
	public static final CodeMsg MIAOSHA_FAIL = new CodeMsg(500504,"秒杀错误");
	public static final CodeMsg ACCESS_LIMIT = new CodeMsg(500505,"访问频繁，请稍后再试");

	private CodeMsg(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}
	
	public int getCode() {
		return code;
	}
	public String getMsg() {
		return msg;
	}
}
