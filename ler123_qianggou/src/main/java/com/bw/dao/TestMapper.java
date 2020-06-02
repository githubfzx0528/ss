package com.bw.dao;

import com.bw.pojo.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TestMapper {
    @Select("select * from user")
    List<User> findAll();
    @Insert("insert into user (id,name) values (#{id},#{name})")
    void insert(User user);
}
