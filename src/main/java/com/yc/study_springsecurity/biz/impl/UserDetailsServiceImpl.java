package com.yc.study_springsecurity.biz.impl;

import com.yc.study_springsecurity.bean.User;
import com.yc.study_springsecurity.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
//重写UserDetailsService的loadUserByUsername方法
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserDao mapper;


    PasswordEncoder passwordEncoder=new BCryptPasswordEncoder();


    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        User user =mapper.findByuname(name);
        if (user==null){
            return null;
        }else {
            //创建一个权限的集合
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            //添加获取权限
            authorities.add(new SimpleGrantedAuthority(user.getRole()));
            //把对象信息（用户名，密码，权限）存入对象，返回该对象，controller层直接调用
           //如果数据库未加密需要添加以下注释的两行代码
            // org.springframework.security.core.userdetails.User user2 =new org.springframework.security.core.userdetails.User(user.getUsername(), passwordEncoder.encode(user.getPwd()), authorities);
            org.springframework.security.core.userdetails.User user2 =new org.springframework.security.core.userdetails.User(user.getUname(), user.getUpwd(), authorities);
           // System.out.println("管理员信息："+user.getUsername()+"   "+passwordEncoder.encode(user.getPwd())+"  "+user2.getAuthorities());
            return user2;
        }

    }
}
