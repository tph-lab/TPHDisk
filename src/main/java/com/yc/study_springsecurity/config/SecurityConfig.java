package com.yc.study_springsecurity.config;


import com.yc.study_springsecurity.biz.impl.UserDetailsServiceImpl;
import com.yc.study_springsecurity.validate.ValidateCodeFilter;
import com.yc.study_springsecurity.validate.properties.SecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.social.connect.web.HttpSessionSessionStrategy;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;

import static com.yc.study_springsecurity.validate.ValidateCodeController.SESSION_KEY;

//WebSecurityConfigurerAdapter 是SpringSecurity 提供的用于我们扩展自己的配置
@Configuration
@EnableWebSecurity//可以不写
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private AuthenticationFailureHandler myFailHandler;



    //通过Java的方式 配置用户名/密码
    //在这里完成获得数据库中的用户信息
    //密码一定要加密
    //认证
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //下面这两行配置表示在内存中配置了两个用户
//        auth.inMemoryAuthentication()
//                .withUser("admin").roles("admin").password("a")
//                .and()
//                .withUser("user").roles("user").password("a");


        //Spring Security 提供了BCryptPasswordEncoder类,实现Spring的PasswordEncoder接口使用BCrypt强哈希方法来加密密码。
        auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
    }


    //可以自定义加密方式，实现PasswordEncoder接口，
    // springsercurity后面的版本必须指定PasswordEncoder实现类，但如果不想加密的话，也可以通过空实现的方式
//    @Bean
//    PasswordEncoder passwordEncoder() {
//        return new JWTPasswordEncoder();
//    }




    @Override
    protected void configure(HttpSecurity http) throws Exception {

        /**
         * 创建 验证码 过滤器 ，并将该过滤器的Handler 设置成自定义登录失败处理器
         */
        ValidateCodeFilter validateCodeFilter = new ValidateCodeFilter();
        validateCodeFilter.setFailureHandler(myFailHandler);
        //将 securityproperties 设置进去
        validateCodeFilter.setSecurityProperties(securityProperties);
        //调用 装配 需要图片验证码的 url 的初始化方法
        validateCodeFilter.afterPropertiesSet();

        http
                //在UsernamePasswordAuthenticationFilter 过滤器前 加一个过滤器 来搞验证码
                .addFilterBefore(validateCodeFilter,UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()//开启登录配置
                // 如果有允许匿名的url，填在下面,这些url可以不经过过滤
                .antMatchers("/login.html","/register.html","/register","/code/image").permitAll()
                //注意该处要与数据库的ROLE_后面部分保持一致，大小写也要一致
                .antMatchers("/hello").hasRole("ADMIN")//表示访问 /hello 这个接口，需要具备 ADMIN 这个角色
                .anyRequest().authenticated()//表示剩余的其他接口，任何用户登录之后就能访问
                .and()
                //开启表单登录，该方法会应用 FormLoginConfigurer 到HttpSecurity上，后续会被转换为对应的Filter
                .formLogin()
                //定义登录页面，未登录时，访问一个需要登录之后才能访问的接口，会自动跳转到该页面
                .loginPage("/login.html")//默认跳转的是springsecurity自带的登录界面
                //默认是 /login，但是当配置了.loginPage("/login.html")，默认值就变成了/login.html
                .loginProcessingUrl("/doLogin")
                // 设置登陆成功页
                .defaultSuccessUrl("/index.html")
                //定义登录时，用户名的 key，默认为 username
                .usernameParameter("uname")
//                定义登录时，用户密码的 key，默认为 password
                .passwordParameter("password")
                //       登录成功的处理器
//                .successHandler(new AuthenticationSuccessHandler() {
//                    @Override
//                    public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse resp, Authentication authentication) throws IOException, ServletException {
//                        HttpSession session=req.getSession();
//                        String valcode=req.getParameter("yzm");
//                        System.out.println(valcode);
//                        String validateCode= (String) session.getAttribute(SESSION_KEY);
//                        System.out.println(validateCode);
//                        resp.setCharacterEncoding("UTF-8");
//                        Writer writer=resp.getWriter();
//                        if(!valcode.equalsIgnoreCase(validateCode)){
//                            resp.sendRedirect("/login.html");
//                            //writer.write("验证码错误....");
//                        }else{
//                            resp.sendRedirect("/index.html");
//                        }
//
//                    }
//                })
                //登录失败的处理器
                .failureHandler(new AuthenticationFailureHandler() {
                    @Override
                    public void onAuthenticationFailure(HttpServletRequest req, HttpServletResponse resp, AuthenticationException exception) throws IOException, ServletException, IOException {
                        resp.setContentType("application/json;charset=utf-8");
                        PrintWriter out = resp.getWriter();
                        out.write("fail");
                        out.flush();
                    }
                })
                //和表单登录相关的接口统统都直接通过
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessHandler(new LogoutSuccessHandler() {
                    @Override
                    public void onLogoutSuccess(HttpServletRequest req, HttpServletResponse resp, Authentication authentication) throws IOException, ServletException {
                        resp.setContentType("application/json;charset=utf-8");
                        PrintWriter out = resp.getWriter();
                        out.write("logout success");
                        out.flush();
                    }
                })
                .permitAll()
                .and()
                .httpBasic()
                .and()
                .csrf().disable();// 关闭CSRF跨域

    }


    //直接过滤掉该地址，即该地址不走 Spring Security 过滤器链
    @Override
    public void configure(WebSecurity web) throws Exception {
        // 设置拦截忽略文件夹，可以对静态资源放行
        web.ignoring().antMatchers("/css/**", "/js/**","/register.html");
    }
}