package com.core.corespringsecurity.security.interceptor;

import lombok.RequiredArgsConstructor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.access.intercept.AbstractSecurityInterceptor;
import org.springframework.security.access.intercept.InterceptorStatusToken;
import org.springframework.security.access.method.MethodSecurityMetadataSource;

@RequiredArgsConstructor
public class CustomMethodSecurityInterceptor extends AbstractSecurityInterceptor implements MethodInterceptor {

    private MethodSecurityMetadataSource methodSecurityMetadataSource;

    @Override
    public Class<?> getSecureObjectClass() {
        return MethodInvocation.class;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        InterceptorStatusToken interceptorStatusToken = super.beforeInvocation(invocation);

        Object result;
        try {
            result = invocation.proceed();
        } finally {
            super.finallyInvocation(interceptorStatusToken);
        }

        return super.afterInvocation(interceptorStatusToken, result);
    }

    public MethodSecurityMetadataSource getSecurityMetadataSource() {
        return this.methodSecurityMetadataSource;
    }

    public SecurityMetadataSource obtainSecurityMetadataSource() {
        return this.methodSecurityMetadataSource;
    }

    public void setSecurityMetadataSource(MethodSecurityMetadataSource newSource) {
        this.methodSecurityMetadataSource = newSource;
    }
}
