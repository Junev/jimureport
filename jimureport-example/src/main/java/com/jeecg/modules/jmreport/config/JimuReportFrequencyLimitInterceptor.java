package com.jeecg.modules.jmreport.config;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 积木报表频率限制拦截器
 * 用于屏蔽"请求过于频繁，请升级商业版!"提示
 */
public class JimuReportFrequencyLimitInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 添加请求头，跳过频率检查
        request.setAttribute("skipFrequencyCheck", true);
        // 设置响应头，避免频率限制
        response.setHeader("X-Rate-Limit-Skip", "true");
        return true;
    }
    
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 在响应中添加标识，表示已处理频率限制问题
        if (modelAndView != null && modelAndView.getViewName() != null && 
            modelAndView.getViewName().contains("frequency")) {
            // 如果视图名包含frequency，则重定向到正常页面
            modelAndView.setViewName("redirect:/");
        }
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 处理完成后无需额外操作
    }
}