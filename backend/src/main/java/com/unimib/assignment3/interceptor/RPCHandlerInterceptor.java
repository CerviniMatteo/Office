package com.unimib.assignment3.interceptor;

import com.unimib.assignment3.notifier.TaskObserverNotifier;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RPCHandlerInterceptor implements HandlerInterceptor {

    @Autowired
    private final TaskObserverNotifier taskObserverNotifier;

    public RPCHandlerInterceptor(TaskObserverNotifier taskObserverNotifier) {
        this.taskObserverNotifier = taskObserverNotifier;
    }

    @Override
    public void afterCompletion(@Nonnull HttpServletRequest request,
                                @Nonnull HttpServletResponse response,
                                @Nonnull Object handler,
                                Exception ex) {
        System.out.println("afterCompletion: notifying observers");
        taskObserverNotifier.notifyAllTaskObservers();
    }
}
