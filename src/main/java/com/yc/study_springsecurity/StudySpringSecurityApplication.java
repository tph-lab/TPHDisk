package com.yc.study_springsecurity;

import com.yc.study_springsecurity.validate.properties.SecurityProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@SpringBootApplication
@EnableConfigurationProperties(SecurityProperties.class)
public class StudySpringSecurityApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudySpringSecurityApplication.class, args);
    }

    @Bean
    public AuthenticationFailureHandler getAuthenticationFailureHandler(){
        return new AuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
                httpServletResponse.sendRedirect("/login.html");
                e.printStackTrace();
            }
        };
    }



    @Bean
    public AuthenticationSuccessHandler getAuthenticationSuccessHandler(){
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
                System.out.println("aaaaaaaaaaaaaaaaaaaaa");
                httpServletResponse.sendRedirect("index.html");
            }
        };
    }


}
