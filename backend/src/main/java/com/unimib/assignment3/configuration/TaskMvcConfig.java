package com.unimib.assignment3.configuration;

import com.unimib.assignment3.interceptor.RPCHandlerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class TaskMvcConfig implements WebMvcConfigurer {

    private final RPCHandlerInterceptor rpcHandlerInterceptor;

    @Autowired
    public TaskMvcConfig(RPCHandlerInterceptor rpcHandlerInterceptor) {
        this.rpcHandlerInterceptor = rpcHandlerInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rpcHandlerInterceptor)
                .addPathPatterns("/task/**State", "/task/acceptTask", "/task/startTask");
        System.out.println("Added interceptor for task/** endpoints");
    }

}
