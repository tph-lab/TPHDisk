package com.yc.study_springsecurity.validate.code;

import com.yc.study_springsecurity.validate.ImageCode;

import com.yc.study_springsecurity.validate.properties.SecurityProperties;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;


public class ImageCodeGenerator implements ValidateCodeGenerator {
    /**
     * 读取了yml文件
     * 引入 Security 配置属性类
     */
    private SecurityProperties securityProperties;


    /**
     * 创建验证码
     */
    @Override
    public ImageCode createCode(ServletWebRequest request) {

       ///////////////////////////////////////////////////////////////////////
        //如果请求中有 width 参数，则用请求中的，否则用 配置属性中的
        int width = ServletRequestUtils.getIntParameter(request.getRequest(),"width",securityProperties.getCode().getImage().getWidth());
        //高度（宽度）
        int height = ServletRequestUtils.getIntParameter(request.getRequest(),"height",securityProperties.getCode().getImage().getHeight());
        //图片验证码字符个数
        int length = securityProperties.getCode().getImage().getLength();
        //过期时间
        int expireIn = securityProperties.getCode().getImage().getExpireIn();
        //////////////////////////////////////////////////////////////////////





        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics g = image.getGraphics();

        Random random = new Random();

        g.setColor(getRandColor(200, 250));
        g.fillRect(0, 0, width, height);
        g.setFont(new Font("Times New Roman", Font.ITALIC, 20));
        g.setColor(getRandColor(160, 200));
        for (int i = 0; i < 155; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int xl = random.nextInt(12);
            int yl = random.nextInt(12);
            g.drawLine(x, y, x + xl, y + yl);
        }







        //////////////////////////////////////////////////////////////////////////////
        //取随机数
        String sRand = "";
        for (int i = 0; i < length; i++) {
            String rand = String.valueOf(random.nextInt(10));
            sRand += rand;
            g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110)));
            g.drawString(rand, 13 * i + 6, 16);
        }
        ///////////////////////////////////////////////////////////////////////////////






        g.dispose();

        return new ImageCode(image, sRand, expireIn);
    }


    /**
     * 生成随机背景条纹
     */
    private Color getRandColor(int fc, int bc) {
        Random random = new Random();
        if (fc > 255) {
            fc = 255;
        }
        if (bc > 255) {
            bc = 255;
        }
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }

    public SecurityProperties getSecurityProperties() {
        return securityProperties;
    }

    public void setSecurityProperties(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }



}
