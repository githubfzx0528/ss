package com.bw.vo;


import com.bw.pojo.MiaoShaUser;

public class GoodsDetailVo {
	private long miaoshaStatus = 0;
	private long remainSeconds = 0;
	private GoodsVo goods ;
	private MiaoShaUser user;
	public GoodsDetailVo(long miaoshaStatus, long remainSeconds, GoodsVo goods, MiaoShaUser user) {
		this.miaoshaStatus = miaoshaStatus;
		this.remainSeconds = remainSeconds;
		this.goods = goods;
		this.user = user;
	}
	public long getMiaoshaStatus() {
		return miaoshaStatus;
	}
	public void setMiaoshaStatus(long miaoshaStatus) {
		this.miaoshaStatus = miaoshaStatus;
	}
	public long getRemainSeconds() {
		return remainSeconds;
	}
	public void setRemainSeconds(long remainSeconds) {
		this.remainSeconds = remainSeconds;
	}
	public GoodsVo getGoods() {
		return goods;
	}
	public void setGoods(GoodsVo goods) {
		this.goods = goods;
	}
	public MiaoShaUser getUser() {
		return user;
	}
	public void setUser(MiaoShaUser user) {
		this.user = user;
	}
	@Override
	public String toString() {
		return "GoodsDetailVo [miaoshaStatus=" + miaoshaStatus + ", remainSeconds=" + remainSeconds + ", goods=" + goods
				+ ", user=" + user + "]";
	}
	
	 
	
	
}