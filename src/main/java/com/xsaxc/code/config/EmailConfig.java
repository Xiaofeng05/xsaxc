package com.xsaxc.code.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * @author 师晓峰
 * @version V1.0
 * @date 2023/9/30 16:00
 * @Description:
 *      邮件配置类
 */
@Configuration
public class EmailConfig {


    /**
     * 获取邮件发送实例
     * @return
     */
    @Bean
    public MailSender mailSender(){
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        // 指定用来发Email的邮件服务器主机
        mailSender.setHost("smtp.qq.com");
        // 默认端口，标准的SMTP端口
        mailSender.setPort(587);
        mailSender.setUsername("1690514096@qq.com");
        // 得到的授权码
        mailSender.setPassword("vmtfxlgvhcnjfbfh");
        return mailSender;
    }
}
