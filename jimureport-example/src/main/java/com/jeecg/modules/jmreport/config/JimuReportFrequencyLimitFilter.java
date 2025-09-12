package com.jeecg.modules.jmreport.config;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * 积木报表频率限制过滤器
 * 用于屏蔽"请求过于频繁，请升级商业版!"提示
 */
@Component
@Order(1)
public class JimuReportFrequencyLimitFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // 添加跳过频率检查的属性
        httpRequest.setAttribute("skipFrequencyCheck", true);
        
        // 创建响应包装器以捕获响应内容
        JimuReportResponseWrapper responseWrapper = new JimuReportResponseWrapper(httpResponse);
        
        // 继续执行过滤器链
        chain.doFilter(request, responseWrapper);
        
        // 获取响应内容
        String responseData = responseWrapper.getCaptureAsString();
        
        // 检查是否包含频率限制提示
        if (responseData.startsWith("请求过于频繁")
                || responseData.startsWith("Too many requests，Please upgrade the commercial version！")) {
            // 清空响应内容，返回空的JSON对象
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            httpResponse.setContentType("application/json;charset=UTF-8");
            httpResponse.setCharacterEncoding("UTF-8");
            PrintWriter writer = httpResponse.getWriter();
            writer.write("请求过于频繁，请稍后重试");
            writer.flush();
        } else {
            // 正常响应，将内容写回
            byte[] responseDataBytes = responseWrapper.getCaptureAsBytes();
            httpResponse.getOutputStream().write(responseDataBytes);
            
            // 只有当原始响应设置了内容类型时才设置，避免覆盖
            if (responseWrapper.getContentType() != null) {
                httpResponse.setContentType(responseWrapper.getContentType());
            }
            if (responseWrapper.getCharacterEncoding() != null) {
                httpResponse.setCharacterEncoding(responseWrapper.getCharacterEncoding());
            }
        }
    }
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 初始化操作
    }
    
    @Override
    public void destroy() {
        // 销毁操作
    }
}