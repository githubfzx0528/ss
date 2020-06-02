package com.bw.dao;

import com.bw.pojo.MiaoshaGoods;
import com.bw.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface GoodsMapper {

    @Select("select * from goods g left join miaosha_goods mg on g.id=mg.goods_id")
    List<GoodsVo> findGoods();

    @Select("select * from goods g left join miaosha_goods mg on g.id=mg.goods_id where g.id=#{id}")
    GoodsVo findMiaoShaGoods(Long id);

    @Select("select * from miaosha_goods")
    List<MiaoshaGoods> findAllMiaoShaGoods();
}
