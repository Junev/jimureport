package com.jeecg.modules.jmreport.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;

/**
 * 积木报表频率限制配置类
 * 用于屏蔽"请求过于频繁，请升级商业版!"提示
 */
@Configuration
public class JimuReportFrequencyLimitConfig implements WebMvcConfigurer {
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册自定义拦截器，用于处理频率限制相关逻辑
        // InterceptorRegistration registration = registry.addInterceptor(new JimuReportFrequencyLimitInterceptor());
        // registration.addPathPatterns("/jmreport/**");
        // registration.excludePathPatterns("/jmreport/view/**");
        // // 设置拦截器的优先级，确保在其他可能影响的拦截器之前执行
        // registration.order(0);
    }
    
    @PostConstruct
    public void init() {
        // 初始化方法，可用于加载频率限制配置
        System.out.println("初始化积木报表频率限制配置");
        // TODO: 添加具体的初始化逻辑，如从配置文件加载限制规则
    }
}