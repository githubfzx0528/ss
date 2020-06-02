package com.bw.service;

import com.bw.dao.OrderInfoMapper;
import com.bw.pojo.OrderInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderInfoService {
    @Autowired
    OrderInfoMapper orderInfoMapper;

    public OrderInfo findOrderInfo(Long orderId) {
        return orderInfoMapper.findOrderInfo(orderId);
    }

    public Boolean updateOrder(String orderId) {
        int i=orderInfoMapper.updateOrder(orderId);
        return i>0;
    }
}
