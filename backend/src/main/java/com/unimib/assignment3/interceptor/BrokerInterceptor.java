package com.unimib.assignment3.interceptor;

import com.unimib.assignment3.notifier.TaskObserverNotifier;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class BrokerInterceptor implements HandlerInterceptor {

    @Autowired
    private final TaskObserverNotifier taskObserverNotifier;

    public BrokerInterceptor(TaskObserverNotifier taskObserverNotifier) {
        this.taskObserverNotifier = taskObserverNotifier;
    }

    @Override
    public void afterCompletion(@Nonnull HttpServletRequest request,
                                @Nonnull HttpServletResponse response,
                                @Nonnull Object handler,
                                Exception ex) {
        if(request.getRequestURI().endsWith("/deleteTask")) {
            taskObserverNotifier.notifyOnDeleteAllTaskObservers((Long) request.getAttribute("taskId"));
        }else{
            taskObserverNotifier.notifyOnFetchAllTaskObservers((Long) request.getAttribute("taskId"));
        }
        System.out.println("afterCompletion: notifying observers");
    }
}
