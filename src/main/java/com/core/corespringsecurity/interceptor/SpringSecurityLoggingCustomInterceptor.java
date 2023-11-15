package com.core.corespringsecurity.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 단순히, SpringSecurity의 로그를 확인하고 싶어 작성한 Interceptor이다.
 */
@Slf4j
public class SpringSecurityLoggingCustomInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (SecurityContextHolder.getContext().getAuthentication() != null
                && SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserDetails userDetails
        ) {
            log.info("\nLogin\t Username :: {} \n\t\t Password :: {} \n\t\t Authorities :: {}", userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
        }

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
