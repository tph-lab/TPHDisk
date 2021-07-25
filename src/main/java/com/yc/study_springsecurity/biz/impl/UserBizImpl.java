package com.yc.study_springsecurity.biz.impl;

import com.yc.study_springsecurity.bean.User;
import com.yc.study_springsecurity.biz.UserBiz;
import com.yc.study_springsecurity.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserBizImpl implements UserBiz {

    @Autowired
    UserDao userDao;

    public boolean insert(User user){
        encryptPassword(user);
        if(userDao.save(user)!=null)
            return true;
        else
            return false;
    };

    public void encryptPassword(User user){
        String password = user.getUpwd();
        password = new BCryptPasswordEncoder().encode(password);
        System.out.println(password);
        user.setUpwd(password);
    }
}
