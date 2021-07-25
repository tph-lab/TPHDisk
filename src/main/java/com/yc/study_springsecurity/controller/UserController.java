package com.yc.study_springsecurity.controller;


import com.yc.study_springsecurity.bean.User;
import com.yc.study_springsecurity.biz.impl.UserBizImpl;
import com.yc.study_springsecurity.biz.impl.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class UserController {
    @Autowired
    UserBizImpl userBizimpl;


    @GetMapping("/userLogin")
    public String hello() {
        return "login.html";
    }

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @GetMapping("/register")
    public String register(String uname,String password){
        User user=new User();
        user.setUname(uname);
        user.setUpwd(password);
        user.setRole("ROLE_USER");
        if(userBizimpl.insert(user)==true){
            return "login.html";
        }else{
            return "register.html";
        }
    }

}
