package com.bw.dao;

import com.bw.pojo.OrderInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface OrderInfoMapper {

    @Select("select * from order_info where id=#{orderId}")
    OrderInfo findOrderInfo(Long orderId);

    @Update("update order_info set status=1 ,pay_date=now() where id=#{orderId}")
    int updateOrder(String orderId);
}
