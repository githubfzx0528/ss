package com.bw.service;

import com.bw.dao.GoodsMapper;
import com.bw.pojo.MiaoshaGoods;
import com.bw.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsService {
    @Autowired
    GoodsMapper goodsMapper;

    public List<GoodsVo> findGoods() {
        List<GoodsVo> list = goodsMapper.findGoods();
        return list;
    }

    public GoodsVo findMiaoShaGoods(Long id) {
        GoodsVo goodsVo=goodsMapper.findMiaoShaGoods(id);
        return goodsVo;
    }

    public List<MiaoshaGoods> findAllMiaoShaGoods() {
        return goodsMapper.findAllMiaoShaGoods();
    }
}
