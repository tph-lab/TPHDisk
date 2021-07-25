package com.yc.study_springsecurity.dao;

import com.yc.study_springsecurity.bean.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDao extends JpaRepository<User,Integer> {

    //后面接属性名，大小写也要一直
    User findByuname(String uname);
}
