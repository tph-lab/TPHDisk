package com.yc.study_springsecurity.validate.properties;



import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Security 属性 类
 *可以这样使用
 */
@Data
@ConfigurationProperties(prefix = "tph.security")
public class SecurityProperties {
//    /**
//     * 浏览器 属性类
//     */
//    private BrowserProperties browser = new BrowserProperties();

    /**
     * 验证码 属性类
     */
    private ValidateCodeProperties code = new ValidateCodeProperties();


}