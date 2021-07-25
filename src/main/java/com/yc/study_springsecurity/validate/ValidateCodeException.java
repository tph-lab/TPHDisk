package com.yc.study_springsecurity.validate;


import org.springframework.security.core.AuthenticationException;

/**
 * 自定义 验证码异常类
 *
 */
public class ValidateCodeException extends AuthenticationException {
    public ValidateCodeException(String msg) {
        super(msg);
    }
}