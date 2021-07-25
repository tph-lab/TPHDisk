package com.yc.study_springsecurity.validate.code;

import com.yc.study_springsecurity.validate.ImageCode;
import org.springframework.web.context.request.ServletWebRequest;

/**
 * 验证码生成器
 *
 */
public interface ValidateCodeGenerator {
    /**
     * 创建验证码
     */
    ImageCode createCode(ServletWebRequest request);
}