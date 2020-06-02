package com.bw.rabbit;

import com.bw.redis.RedisService;
import com.bw.service.GoodsService;
import com.bw.service.MiaoShaService;
import com.bw.service.OrderInfoService;
import com.bw.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class MQReceiver {

		private static Logger log = LoggerFactory.getLogger(MQReceiver.class);
		
		@Autowired
		RedisService redisService;
		
		@Autowired
		GoodsService goodsService;
		
		@Autowired
		OrderInfoService orderInfoService;
		
		@Autowired
		MiaoShaService miaoshaService;


		@RabbitListener(queues=MQConfig.MIAOSHA_QUEUE)
		public void miaoshaReceive(String message){
			System.out.printf("send message"+message+"\n");
			MiaoshaMessage miaoshaMessage = redisService.stringToBean(message, MiaoshaMessage.class);
			GoodsVo goodsVo = goodsService.findMiaoShaGoods(miaoshaMessage.getGoodsId());
			miaoshaService.AddMiaoShaOrder(miaoshaMessage.getUser(),goodsVo);
		}
		
//		@RabbitListener(queues=MQConfig.MIAOSHA_QUEUE)
//		public void receive(String message) {
//			log.info("receive message:"+message);
//			MiaoshaMessage mm  = RedisService.stringToBean(message, MiaoshaMessage.class);
//			MiaoshaUser user = mm.getUser();
//			long goodsId = mm.getGoodsId();
//			
//			GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
//	    	int stock = goods.getStockCount();
//	    	if(stock <= 0) {
//	    		return;
//	    	}
//	    	//判断是否已经秒杀到了
//	    	MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
//	    	if(order != null) {
//	    		return;
//	    	}
//	    	//减库存 下订单 写入秒杀订单
//	    	miaoshaService.miaosha(user, goods);
//		}
	
		@RabbitListener(queues=MQConfig.QUEUE)
		public void receive(String message) {
			log.info("receive message:"+message);
		}
		
		//-------------------------------------------
		@RabbitListener(queues=MQConfig.TOPIC_QUEUE1)
		public void receiveTopic1(String message) {
			log.info(" topic  queue1 message:"+message);
		}
		
		@RabbitListener(queues=MQConfig.TOPIC_QUEUE2)
		public void receiveTopic2(String message) {
			log.info(" topic  queue2 message:"+message);
		}
		
		//------------------------------------------------
		@RabbitListener(queues=MQConfig.HEADER_QUEUE)
		public void receiveHeaderQueue(byte[] message) {
			log.info(" header  queue message:"+new String(message));
		}
		
		
}
