package com.yc.study_springsecurity.biz;

import com.yc.study_springsecurity.bean.User;
import com.yc.study_springsecurity.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


public interface UserBiz {

    public boolean insert(User user);

    public void encryptPassword(User user);

}
