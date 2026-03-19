package com.unimib.assignment3.configuration;

import com.unimib.assignment3.interceptor.BrokerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class TaskMvcConfig implements WebMvcConfigurer {

    private final BrokerInterceptor brokerInterceptor;

    @Autowired
    public TaskMvcConfig(BrokerInterceptor brokerInterceptor) {
        this.brokerInterceptor = brokerInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(brokerInterceptor)
                .addPathPatterns("/task/**State", "/task/acceptTask", "/task/startTask", "/task/createTask", "/task/deleteTask");
        System.out.println("Added interceptor for task/** endpoints");
    }

}
