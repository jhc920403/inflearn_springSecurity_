package com.core.corespringsecurity.security.config;

import com.core.corespringsecurity.security.factory.MethodResourceMapFactoryBean;
import com.core.corespringsecurity.security.interceptor.CustomMethodSecurityInterceptor;
import com.core.corespringsecurity.security.processor.ProtectPointcutPostProcessor;
import com.core.corespringsecurity.service.SecurityResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.intercept.RunAsManager;
import org.springframework.security.access.method.MapBasedMethodSecurityMetadataSource;
import org.springframework.security.access.method.MethodSecurityMetadataSource;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

    private final SecurityResourceService securityResourceService;

    @Override
    protected MethodSecurityMetadataSource customMethodSecurityMetadataSource() {
        return mapBasedMethodSecurityMetadataSource();
    }

    @Bean
    public MapBasedMethodSecurityMetadataSource mapBasedMethodSecurityMetadataSource() {
        return new MapBasedMethodSecurityMetadataSource(methodResourceMapFactoryBean().getObject());
    }

    @Bean
    public MethodResourceMapFactoryBean methodResourceMapFactoryBean() {
        MethodResourceMapFactoryBean MethodResourceMapFactoryBean = new MethodResourceMapFactoryBean();
        MethodResourceMapFactoryBean.setSecurityResourceService(securityResourceService);
        MethodResourceMapFactoryBean.setResourceType("method");
        return MethodResourceMapFactoryBean;
    }

    @Bean
    @Profile("pointcut")
    public MethodResourceMapFactoryBean pointcutResourcesMapFactoryBean(){
        MethodResourceMapFactoryBean MethodResourceMapFactoryBean = new MethodResourceMapFactoryBean();
        MethodResourceMapFactoryBean.setSecurityResourceService(securityResourceService);
        MethodResourceMapFactoryBean.setResourceType("pointcut");
        return MethodResourceMapFactoryBean;
    }

    /**
     * ProtectPointcutPostProcessor : Bean의 생성 전후 설정 변경을 위해 사용하는 클리스
     */
    @Bean
    @Profile("pointcut")
    public ProtectPointcutPostProcessor protectPointcutPostProcessor(){
        ProtectPointcutPostProcessor protectPointcutPostProcessor = new ProtectPointcutPostProcessor(mapBasedMethodSecurityMetadataSource());
        protectPointcutPostProcessor.setPointcutMap(pointcutResourcesMapFactoryBean().getObject());
        return protectPointcutPostProcessor;
    }

    /**
     * ProtectPointcutPostProcessor 메소드는 final이며, 접근 제한자로 인해 new하여 객체를 생성할 수 없어, 명칭으로 가져오는 방법밖에 없다.
     * 이를 보완하기 위하여 ProtectPointcutPostProcessor 클래스를 별도로 생성하여 접근 및 생성에 관련한 코드를 보완한 내용이 위 코드이다.
     */
    /*
    @Bean
    @Profile("pointcut")
    BeanPostProcessor protectPointcutPostProcessor() throws Exception {

        Class<?> clazz = Class.forName("org.springframework.security.config.method.ProtectPointcutPostProcessor");
        Constructor<?> declaredConstructor = clazz.getDeclaredConstructor(MapBasedMethodSecurityMetadataSource.class);
        declaredConstructor.setAccessible(true);
        Object instance = declaredConstructor.newInstance(mapBasedMethodSecurityMetadataSource());
        Method setPointcutMap = instance.getClass().getMethod("setPointcutMap", Map.class);
        setPointcutMap.setAccessible(true);
        setPointcutMap.invoke(instance, pointcutResourcesMapFactoryBean().getObject());

        return (BeanPostProcessor)instance;
    }
    */

    @Bean
    public CustomMethodSecurityInterceptor customMethodSecurityInterceptor(MapBasedMethodSecurityMetadataSource methodSecurityMetadataSource) {
        CustomMethodSecurityInterceptor customMethodSecurityInterceptor =  new CustomMethodSecurityInterceptor();
        customMethodSecurityInterceptor.setAccessDecisionManager(accessDecisionManager());
        customMethodSecurityInterceptor.setAfterInvocationManager(afterInvocationManager());
        customMethodSecurityInterceptor.setSecurityMetadataSource(methodSecurityMetadataSource);
        RunAsManager runAsManager = runAsManager();
        if (runAsManager != null) {
            customMethodSecurityInterceptor.setRunAsManager(runAsManager);
        }

        return customMethodSecurityInterceptor;
    }
}
