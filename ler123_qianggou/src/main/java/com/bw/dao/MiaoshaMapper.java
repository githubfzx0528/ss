package com.bw.dao;

import com.bw.pojo.MiaoshaGoods;
import com.bw.pojo.MiaoshaOrder;
import com.bw.pojo.OrderInfo;
import org.apache.ibatis.annotations.*;

@Mapper
public interface MiaoshaMapper {
    @Select("select * from miaosha_order where user_id=#{userid} and goods_id=#{goodsid}")
    MiaoshaOrder findByMiaoShaoOrderByIdAndByGoodsId(@Param("userid") Long userid,@Param("goodsid") Long goodsid);

    @Update("update miaosha_goods set stock_count=stock_count-1 where goods_id=#{goodsId} and stock_count>0")
    int reduceStock(Long goodsId);

    @Insert("insert into order_info(user_id, goods_id, goods_name, goods_count, goods_price, goods_channel, status, create_date)" +
            "values(#{userId}, #{goodsId}, #{goodsName}, #{goodsCount}, #{goodsPrice}, #{goodsChannel},#{status},#{createDate} )")
    @SelectKey(keyColumn = "id", keyProperty = "id", resultType =long.class,before = false,statement = "select last_insert_id()")
    void insertOrderInfo(OrderInfo orderInfo);

    @Insert("insert into miaosha_order (user_id, goods_id, order_id)values(#{userId}, #{goodsId}, #{orderId})")
    void insertMiaoShaOrder(MiaoshaOrder miaoshaOrder);

    @Update("update miaosha_goods set stock_count = #{stockCount} where goods_id = #{goodsId}")
    void restStock(MiaoshaGoods miaoshaGoods);

    @Delete("delete from order_info")
    void deleteOrders();

    @Delete("delete from miaosha_order")
    void deleteMiaoshaOrders();

}
