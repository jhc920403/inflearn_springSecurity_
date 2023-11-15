package com.core.corespringsecurity.config;

import com.core.corespringsecurity.repository.AccessIpRepository;
import com.core.corespringsecurity.repository.ResourceRepository;
import com.core.corespringsecurity.service.SecurityResourceService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Bean을 등록하고 관리하는 설정 클래스이다.
 */
@Configuration
public class AppConfig {

    @Bean
    public SecurityResourceService securityResourceService(
            ResourceRepository resourceRepository
            , AccessIpRepository accessIpRepository
    ) {
        SecurityResourceService securityResourceService = new SecurityResourceService(resourceRepository, accessIpRepository);
        return securityResourceService;
    }
}
