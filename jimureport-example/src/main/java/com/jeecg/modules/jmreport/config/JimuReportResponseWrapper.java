package com.jeecg.modules.jmreport.config;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * 响应包装器，用于拦截和修改包含频率限制提示的响应
 */
public class JimuReportResponseWrapper extends HttpServletResponseWrapper {
    
    private final ByteArrayOutputStream capture;
    private PrintWriter output;
    private ServletOutputStream outputStream;
    private String characterEncoding = StandardCharsets.UTF_8.name();
    private boolean writerUsed = false;
    private boolean outputStreamUsed = false;
    
    public JimuReportResponseWrapper(HttpServletResponse response) {
        super(response);
        capture = new ByteArrayOutputStream(response.getBufferSize());
    }
    
    @Override
    public PrintWriter getWriter() throws IOException {
        if (outputStreamUsed) {
            throw new IllegalStateException("getOutputStream() has already been called on this response");
        }
        
        if (output == null) {
            output = new PrintWriter(new java.io.OutputStreamWriter(capture, characterEncoding));
        }
        writerUsed = true;
        return output;
    }
    
    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (writerUsed) {
            throw new IllegalStateException("getWriter() has already been called on this response");
        }
        
        if (outputStream == null) {
            outputStream = new ServletOutputStream() {
                @Override
                public boolean isReady() {
                    return false;
                }

                @Override
                public void setWriteListener(WriteListener writeListener) {
                }

                @Override
                public void write(int b) throws IOException {
                    capture.write(b);
                }
            };
        }
        outputStreamUsed = true;
        return outputStream;
    }
    
    @Override
    public void setCharacterEncoding(String charset) {
        super.setCharacterEncoding(charset);
        this.characterEncoding = charset;
    }
    
    @Override
    public void setContentType(String type) {
        super.setContentType(type);
        // 如果内容类型包含字符编码信息，则提取并设置
        if (type != null && type.toLowerCase().contains("charset=")) {
            String[] parts = type.split(";");
            for (String part : parts) {
                part = part.trim();
                if (part.toLowerCase().startsWith("charset=")) {
                    this.characterEncoding = part.substring(8).trim();
                    break;
                }
            }
        }
    }
    
    public byte[] getCaptureAsBytes() throws IOException {
        if (output != null) {
            output.flush();
        }
        return capture.toByteArray();
    }
    
    public String getCaptureAsString() throws IOException {
        return new String(getCaptureAsBytes(), characterEncoding);
    }
    
    public String getCharacterEncoding() {
        return characterEncoding;
    }
}