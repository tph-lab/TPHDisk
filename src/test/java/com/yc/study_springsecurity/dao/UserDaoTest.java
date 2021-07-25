package com.yc.study_springsecurity.dao;

import com.yc.study_springsecurity.bean.User;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;


@RunWith(SpringRunner.class)
@SpringBootTest
class UserDaoTest {

    @Autowired
    UserDao userDao;


    @Test
    void selectlimit() {
        User user=userDao.findByuname("a");
        System.out.println(user);
    }

    @Test
    void selectAll(){
        userDao.findAll();
    }
}