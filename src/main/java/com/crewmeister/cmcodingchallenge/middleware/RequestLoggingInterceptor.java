package com.crewmeister.cmcodingchallenge.middleware;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;

@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute("startTime", Instant.now());

        logger.info("Incoming Request: {} {}",
                request.getMethod(), request.getRequestURI());

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        Instant startTime = (Instant) request.getAttribute("startTime");
        long executionTime = startTime != null ? Instant.now().toEpochMilli() - startTime.toEpochMilli() : 0;

        logger.info("Response: {} Status: {} Execution Time: {} ms",
                request.getRequestURI(), response.getStatus(), executionTime);
    }
}
