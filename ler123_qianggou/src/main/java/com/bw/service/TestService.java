package com.bw.service;

import com.bw.dao.TestMapper;
import com.bw.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TestService {
    @Autowired
    TestMapper userMapper;

    public List<User> findAll() {
        return userMapper.findAll();
    }

    @Transactional
    public Boolean insert() {
        User user = new User();
        user.setId(6);
        user.setName("张三");
        userMapper.insert(user);
        user.setId(1);
        user.setName("李四");
        userMapper.insert(user);
        return true;
    }
}
