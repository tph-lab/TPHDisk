package com.yc.study_springsecurity.validate.properties;


import lombok.Data;

/**
 * 验证码 配置类
 *
 */
@Data
public class ValidateCodeProperties {
    /**
     * 图形验证码 配置属性
     */
    private ImageCodeProperties image = new ImageCodeProperties();


}