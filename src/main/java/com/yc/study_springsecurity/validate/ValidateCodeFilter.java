package com.yc.study_springsecurity.validate;


import com.yc.study_springsecurity.validate.properties.SecurityProperties;
import com.yc.study_springsecurity.validate.properties.ValidateCodeProperties;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 验证码 过滤器
 *
 */
@Component
public class ValidateCodeFilter  extends OncePerRequestFilter implements InitializingBean {

    //打印日志信息
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 登录失败处理器
     */
    @Autowired
    private AuthenticationFailureHandler failureHandler;

    /**
     * Session 对象
     */
    private SessionStrategy sessionStrategy = new HttpSessionSessionStrategy();

    /**
     * 创建一个Set 集合 存放 需要验证码的 urls
     */
    private Set<String> urls = new HashSet<>();

    /**
     * security applicaiton  配置属性
     */
    @Autowired
    private SecurityProperties securityProperties;

    /**
     * spring的一个工具类：用来判断 两字符串 是否匹配
     */
    private AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     * 这个方法是 InitializingBean 接口下的一个方法， 在初始化配置完成后 运行此方法
     */
    @Override
    public void afterPropertiesSet() throws ServletException {
        super.afterPropertiesSet();
        ValidateCodeProperties code = securityProperties.getCode();
        logger.info(String.valueOf(code));
        //将 application 配置中的 url 属性进行 切割
        //1.通过application.yml的设置
        //2.通过自己添加url.add
        String[] configUrls = StringUtils.splitByWholeSeparatorPreserveAllTokens(securityProperties.getCode().getImage().getUrl(), ",");
        //添加到 Set 集合里
        urls.addAll(Arrays.asList(configUrls));
        //因为登录请求一定要有验证码 ，所以直接 add 到set 集合中
        urls.add("/doLogin");
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, IOException {

        boolean action = false;
        for (String url:urls){
            //如果请求的url 和 配置中的url 相匹配
            if (pathMatcher.match(url,request.getRequestURI())){
                action = true;
            }
        }

        //拦截请求
        if (action){
            logger.info("拦截成功"+request.getRequestURI());
            //如果是登录请求
            try {
                validate(new ServletWebRequest(request));
            }catch (ValidateCodeException exception){
                //返回错误信息给 失败处理器
                failureHandler.onAuthenticationFailure(request,response,exception);
                return;
            }

        }
        //不做任何处理，调用后面的 过滤器
        filterChain.doFilter(request,response);
    }

    private void validate(ServletWebRequest request) throws ServletRequestBindingException {
        //从session中取出 验证码
        ImageCode codeInSession = (ImageCode) sessionStrategy.getAttribute(request,ValidateCodeController.SESSION_KEY);
        //从request 请求中 取出 验证码
        String codeInRequest = ServletRequestUtils.getStringParameter(request.getRequest(),"imageCode");

        if (StringUtils.isBlank(codeInRequest)){
            logger.info("验证码不能为空");
            throw new ValidateCodeException("验证码不能为空");
        }
        if (codeInSession == null){
            logger.info("验证码不存在");
            throw new ValidateCodeException("验证码不存在");
        }
        if (codeInSession.isExpried()){
            logger.info("验证码已过期");
            sessionStrategy.removeAttribute(request,ValidateCodeController.SESSION_KEY);
            throw new ValidateCodeException("验证码已过期");
        }
        if (!StringUtils.equals(codeInSession.getCode(),codeInRequest)){
            logger.info("验证码不匹配"+"codeInSession:"+codeInSession.getCode() +", codeInRequest:"+codeInRequest);
            throw new ValidateCodeException("验证码不匹配");
        }
        //把对应 的 session信息  删掉
        sessionStrategy.removeAttribute(request,ValidateCodeController.SESSION_KEY);

    }

    /**
     * 失败 过滤器 getter and setter 方法
     */
    public AuthenticationFailureHandler getFailureHandler() {
        return failureHandler;
    }

    public void setFailureHandler(AuthenticationFailureHandler failureHandler) {
        this.failureHandler = failureHandler;
    }
    /**
     * SecurityProperties 属性类 getter and setter 方法
     */
    public SecurityProperties getSecurityProperties() {
        return securityProperties;
    }

    public void setSecurityProperties(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }
}
