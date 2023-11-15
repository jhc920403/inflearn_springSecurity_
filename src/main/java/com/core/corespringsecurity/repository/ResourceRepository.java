package com.core.corespringsecurity.repository;

import com.core.corespringsecurity.domain.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * DB를 활용하여 동적 인가 권한을 구현하기 위한 클래스이다.
 * - url / method / pointcut 을 사용한 인가 권한 데이터를 데이터베이스에서 조회해오게 된다.
 * - orderNum 은 권한 설정의 범위를 위한 것이며, 구체적인(협소한) 경로를 orderNum에 작은 값을 부여하고 보다 큰 범위는 큰 값을 부여한다.
 */
public interface ResourceRepository extends JpaRepository<Resource, Long> {
    Resource findByResourceNameAndHttpMethod(String resourceName, String httpMethod);

    @Query("select r from Resource r join fetch r.roleSet where r.resourceType = 'url' order by r.orderNum desc ")
    List<Resource> findAllResources();

    @Query("select r from Resource r join fetch r.roleSet where r.resourceType = 'method' order by r.orderNum desc")
    List<Resource> findAllMethodResource();

    @Query("select r from Resource r join fetch r.roleSet where r.resourceType = 'pointcut' order by r.orderNum desc")
    List<Resource> findAllPointcutResource();
}
