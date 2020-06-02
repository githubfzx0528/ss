package com.bw.dao;

import com.bw.pojo.MiaoShaUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MiaoShaUserMapper {
    @Select("select * from miaosha_user where id=#{mobile}")
    MiaoShaUser findUser(long mobile);
}
