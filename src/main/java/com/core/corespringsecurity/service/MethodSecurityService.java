package com.core.corespringsecurity.service;

import com.core.corespringsecurity.security.interceptor.CustomMethodSecurityInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.access.method.MapBasedMethodSecurityMetadataSource;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MethodSecurityService {

    private MapBasedMethodSecurityMetadataSource mapBasedMethodSecurityMetadataSource;
    private AnnotationConfigServletWebServerApplicationContext annotationConfigServletWebApplicationContext;
    private CustomMethodSecurityInterceptor methodSecurityInterceptor;

    private Map<String, Object> proxyMap = new HashMap<>();
    private Map<String, ProxyFactory>  advisedMap = new HashMap<>();
    private Map<String, Object> targetMap = new HashMap<>();

    public MethodSecurityService (
            MapBasedMethodSecurityMetadataSource mapBasedMethodSecurityMetadataSource
            , AnnotationConfigServletWebServerApplicationContext annotationConfigServletWebServerApplicationContext
            , CustomMethodSecurityInterceptor methodSecurityInterceptor
    ) {
        this.mapBasedMethodSecurityMetadataSource = mapBasedMethodSecurityMetadataSource;
        this.annotationConfigServletWebApplicationContext = annotationConfigServletWebServerApplicationContext;
        this.methodSecurityInterceptor = methodSecurityInterceptor;
    }

    public void addMethodSecured(String className, String roleName) throws Exception {
        int lastDotIndex = className.lastIndexOf(".");
        String methodName = className.substring(lastDotIndex+1);
        String typeName = className.substring(0, lastDotIndex);
        Class<?> type = ClassUtils.resolveClassName(typeName, ClassUtils.getDefaultClassLoader());
        String beanName = type.getSimpleName().substring(0, 1).toLowerCase() + type.getSimpleName().substring(1);

        ProxyFactory proxyFactory = advisedMap.get(beanName);
        Object target = targetMap.get(beanName);
        if (proxyFactory == null) {
            proxyFactory = new ProxyFactory();

            if(target == null) {
                proxyFactory.setTarget(type.getDeclaredConstructor().newInstance());

            }else{
                proxyFactory.setTarget(target);
            }
        } else {
            int adviceIndex = proxyFactory.indexOf(methodSecurityInterceptor);
            if(adviceIndex == -1) {
                proxyFactory.addAdvice(methodSecurityInterceptor);
            }
        }

        Object proxy = proxyMap.get(beanName);
        if (proxy == null) {
            proxy = proxyFactory.getProxy();
            proxyMap.put(beanName, proxy);

            List<ConfigAttribute> attr = Arrays.asList(new SecurityConfig(roleName));
            mapBasedMethodSecurityMetadataSource.addSecureMethod(type, methodName, attr);

            DefaultSingletonBeanRegistry registry = (DefaultSingletonBeanRegistry) annotationConfigServletWebApplicationContext.getBeanFactory();
            registry.destroySingleton(beanName);
            registry.destroySingleton(beanName);
            registry.registerSingleton(beanName, proxy);
        }
    }

    public void removeMethodSecured(String className) throws Exception {
        int lastDotIndex = className.lastIndexOf(".");
        String typeName = className.substring(0, lastDotIndex);
        Class<?> type = ClassUtils.resolveClassName(typeName, ClassUtils.getDefaultClassLoader());
        String beanName = type.getSimpleName().substring(0, 1).toLowerCase() + type.getSimpleName().substring(1);
        Object newInstance = type.getDeclaredConstructor().newInstance();

        DefaultSingletonBeanRegistry registry = (DefaultSingletonBeanRegistry) annotationConfigServletWebApplicationContext.getBeanFactory();

        ProxyFactory proxyFactory = advisedMap.get(beanName);

        if(proxyFactory != null) {
            proxyFactory.removeAdvice(methodSecurityInterceptor);
        } else {
            registry.destroySingleton(beanName);
            registry.registerSingleton(beanName, newInstance);
            targetMap.put(beanName, newInstance);
        }
    }
}
