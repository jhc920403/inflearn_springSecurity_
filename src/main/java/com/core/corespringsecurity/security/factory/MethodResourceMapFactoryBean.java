package com.core.corespringsecurity.security.factory;

import com.core.corespringsecurity.service.SecurityResourceService;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.security.access.ConfigAttribute;

import java.util.LinkedHashMap;
import java.util.List;

public class MethodResourceMapFactoryBean implements FactoryBean<LinkedHashMap<String, List<ConfigAttribute>>> {

    // 데이터베이스를 사용하여 접근 가능한 권한 및 자원정보를 호출하는 Service 객체이다.
    private SecurityResourceService securityResourceService;
    // Bean을 담을 객체를 생성한다.
    private LinkedHashMap<String, List<ConfigAttribute>> resourcesMap;
    private String resourceType;

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public void setSecurityResourceService(SecurityResourceService securityResourceService) {
        this.securityResourceService = securityResourceService;
    }

    @Override
    public LinkedHashMap<String, List<ConfigAttribute>> getObject() {

        if(resourcesMap == null){
            init();
        }

        return resourcesMap;
    }

    private void init() {
        if ("method".equals(resourceType)) {
            resourcesMap = securityResourceService.getMethodResourceList();
        }else if("pointcut".equals(resourceType)){
            resourcesMap = securityResourceService.getPointcutResourceList();
        }
    }

    @SuppressWarnings("rawtypes")
    public Class<LinkedHashMap> getObjectType() {
        return LinkedHashMap.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
