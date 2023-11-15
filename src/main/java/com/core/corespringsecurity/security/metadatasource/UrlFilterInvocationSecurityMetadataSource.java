package com.core.corespringsecurity.security.metadatasource;

import com.core.corespringsecurity.service.SecurityResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * URL 접근 권한 정보를 처리하기 위해 구현된 클래스이다.
 */
@Slf4j
@Component
public class UrlFilterInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    /*
     * Url 자원에 대한 접근 가능한 권한을 저장하는 객체이다.
     * RequestMatcher : 요청에 대한 정보를 담고 있는 객체이다. ex) /admin
     * ConfigAttribute : DB에서 조회된 권한 정보를 담고 있는 객체이다. ex) ROLE_ADMIN
     *
     * requestMap 가 LinkedHashMap으로 작성된 이유는 정렬 순서와 동일하게 객체에 담아야 되기 때문이다.
     */
    private final LinkedHashMap<RequestMatcher, List<ConfigAttribute>> requestMap;
    private SecurityResourceService securityResourceService;

    public UrlFilterInvocationSecurityMetadataSource(
            LinkedHashMap<RequestMatcher, List<ConfigAttribute>> resourceMap
            , SecurityResourceService securityResourceService
    ) {
        this.requestMap = resourceMap;
        this.securityResourceService = securityResourceService;
    }

    /**
     * DefaultFilterInvocationSecurityMetadataSource의 getAttributes를 참조하여 작성한다.
     * Url 권한 방식과 메소드 권한 방식이 getAttributes를 공통으로 사용하기 때문에 파라미터가 Object 타입이다.
     */
    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        
        HttpServletRequest request = ((FilterInvocation) object).getRequest();  // 사용자 요청 정보 추출하는 구문

        // requestMap.put(new AntPathRequestMatcher("/mypage"), Arrays.asList(new SecurityConfig("ROLE_USER")));

        if(requestMap != null) {
            for(Map.Entry<RequestMatcher, List<ConfigAttribute>> entry : requestMap.entrySet()) {   // 권한 정보가 담겨 있는 객체
                RequestMatcher matcher = entry.getKey();

                if(matcher.matches(request)) {      // 사용자 접근 권한과 접근 요청한 권한정보가 존재하는지 매칭하는 구문
                    return entry.getValue();
                }
            }
        }
        return null;
    }

    /**
     * DefaultFilterInvocationSecurityMetadataSource의 getAllConfigAttributes를 참조하여 작성한다.
     */
    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        Set<ConfigAttribute> allAtrributes = new HashSet<>();

        for(Map.Entry<RequestMatcher, List<ConfigAttribute>> entry : requestMap.entrySet()) {
            allAtrributes.addAll(entry.getValue());
        }

        return allAtrributes;
    }

    /**
     * DefaultFilterInvocationSecurityMetadataSource의 supports를 참조하여 작성한다.
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }

    /**
     * 스프링 시큐리티에서는 실시간으로 수정되는 인가처리에 대한 반영은 지원하지 않는다.
     * 이를 위해서 별도의 데이터베이스를 조회하여 최신화된 실시간 데이터를 반영하여 동작하도록 코드 추가가 필요하다.
     */
    public void reload() {
        LinkedHashMap<RequestMatcher, List<ConfigAttribute>> reloadedMap = securityResourceService.getResourceList();
        Iterator<Map.Entry<RequestMatcher, List<ConfigAttribute>>> iterator = reloadedMap.entrySet().iterator();

        requestMap.clear();

        while(iterator.hasNext()){
            Map.Entry<RequestMatcher, List<ConfigAttribute>> entry = iterator.next();
            requestMap.put(entry.getKey(), entry.getValue());
        }
    }
}
