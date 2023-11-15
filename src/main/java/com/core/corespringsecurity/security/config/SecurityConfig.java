package com.core.corespringsecurity.security.config;

import com.core.corespringsecurity.security.common.FormAuthenticationDetailsSource;
import com.core.corespringsecurity.security.factory.UrlResourceMapFactoryBean;
import com.core.corespringsecurity.security.filter.PermitAllFilter;
import com.core.corespringsecurity.security.handler.FormAccessDeniedHandler;
import com.core.corespringsecurity.security.metadatasource.UrlFilterInvocationSecurityMetadataSource;
import com.core.corespringsecurity.security.provider.FormAuthenticationProvider;
import com.core.corespringsecurity.security.voter.IpAddressVoter;
import com.core.corespringsecurity.service.SecurityResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleHierarchyVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Order(1)
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * ** DataBase 기반 사용자 계정을 저장하는 방식으로 전환하면서 주석처리 **
     * In-Memory 형태로 초기 사용자 계정 생성한다.

     @Bean
     public UserDetailsService initUsers() {
     String password = passwordEncoder().encode("1111");

     UserDetails user = User.builder()
     .username("user")
     .password(password)
     .roles("USER")
     .build();

     UserDetails manager = User.builder()
     .username("manager")
     .password(password)
     .roles("USER", "MANAGER")
     .build();

     UserDetails admin = User.builder()
     .username("admin")
     .password(password)
     .roles("USER", "MANAGER", "ADMIN")
     .build();

     return new InMemoryUserDetailsManager(user, manager, admin);
     }
     */

    /**
     * Spring Security에서는 비밀번호를 안전하게 암호화되도록 제공한다.
     * 패스워드 암호화하는 알고리즘을 설정하며, PasswordEncoderFactories.createDelegatingPasswordEncoder()는 Default 암호화 방식인 bcrypt를 지원한다.
     * - 참고 : https://velog.io/@sunnamgung8/Spring-Security-PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    private final UserDetailsService userDetailsService;
    private final FormAuthenticationDetailsSource formAuthenticationDetailsSource;
    private final AuthenticationSuccessHandler formAuthenticationSuccessHandler;
    private final AuthenticationFailureHandler formAuthenticationFailureHandler;
    private final SecurityResourceService securityResourceService;

    private String[] permitAllResource = {"/", "/login", "/user/login/**"};

    @Bean
    public AuthenticationProvider authenticationProvider() {
        return new FormAuthenticationProvider(userDetailsService, passwordEncoder());
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        FormAccessDeniedHandler accessDeniedHandler = new FormAccessDeniedHandler();
        accessDeniedHandler.setErrorPage("/denied");
        return accessDeniedHandler;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }


    @Override
    protected void configure (AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    /**
     * 동적(DB) Url 권한 체크를 위한 Bean
     */
    @Bean
    public PermitAllFilter customFilterSecurityInterceptor() throws Exception {
        // PermitAllFilter 생성 후 적용
        //FilterSecurityInterceptor filterSecurityInterceptor = new FilterSecurityInterceptor();
        PermitAllFilter filterSecurityInterceptor = new PermitAllFilter(permitAllResource);
        filterSecurityInterceptor.setSecurityMetadataSource(urlFilterInvocationSecurityMetadataSource());
        filterSecurityInterceptor.setAccessDecisionManager(affirmativeBased());
        filterSecurityInterceptor.setAuthenticationManager(authenticationManagerBean());
        return filterSecurityInterceptor;
    }

    /**
     * DB에 저장된 데이터를 바탕으로 Url 인가 권한 구현을 위한 Bean 등록 작업이다.
     */
    @Bean
    public FilterInvocationSecurityMetadataSource urlFilterInvocationSecurityMetadataSource() throws Exception {
        return new UrlFilterInvocationSecurityMetadataSource(urlResourceMapFactoryBean().getObject(), securityResourceService);
    }

    private UrlResourceMapFactoryBean urlResourceMapFactoryBean() {
        UrlResourceMapFactoryBean urlResourceMapFactoryBean = new UrlResourceMapFactoryBean();
        urlResourceMapFactoryBean.setSecurityResourceService(securityResourceService);

        return urlResourceMapFactoryBean;
    }

    private AccessDecisionManager affirmativeBased() {
        AffirmativeBased affirmativeBased = new AffirmativeBased(getAccessDecisionVoters());
        return affirmativeBased;
    }

    private List<AccessDecisionVoter<?>> getAccessDecisionVoters() {

        List<AccessDecisionVoter<? extends Object>> accessDicisionVoters = new ArrayList<>();
        accessDicisionVoters.add(new IpAddressVoter(securityResourceService));
        accessDicisionVoters.add(roleVoter());

        return accessDicisionVoters;
        //return Arrays.asList(new RoleVoter());
    }

    @Bean
    public AccessDecisionVoter<? extends Object> roleVoter() {
        return new RoleHierarchyVoter(roleHierarchy());
    }

    @Bean
    public RoleHierarchyImpl roleHierarchy() {
        return new RoleHierarchyImpl();
    }


    /**
     * formLogin 형태의 인증 방식으로 구현한 SecurityFilterChain
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {

        http
                .authorizeRequests()
                /*
                 * 해당 접근 권한 정보는 Spring Security가 실행될 때 ExpressionBasedFilterInvocationSecurityMetadataSource.java 파일에서 저장된다.
                 * (DB 인가처리로 구현되기 때문에 설정 주석처리)

                .antMatchers("/mypage").hasRole("USER")
                .antMatchers("/messages").hasRole("MANAGER")
                .antMatchers("/config").hasRole("ADMIN")
                .antMatchers("/**").permitAll()
                */
                .anyRequest().authenticated()
        .and()
                .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login_proc")  // login action과 매핑시키게된다. 별도로 Controller에서는 해당 경로를 작성하지 않는다.
                .authenticationDetailsSource(formAuthenticationDetailsSource)
                .defaultSuccessUrl("/")
                .successHandler(formAuthenticationSuccessHandler)     // 인증 성공 후 처리 구현을 위한 클래스를 설정해준다.
                .failureHandler(formAuthenticationFailureHandler)     // 인증 실패 후 처리 구현을 위한 클래스를 설정해준다.
                .permitAll()
        .and()
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler())
        .and()
                // 아래 필터를 적용하는 경우 antMatchers()의 설정은 적용되지 않는다. (참고 : AbstractSecurityInterceptor > attemptAuthorization)
                .addFilterBefore(customFilterSecurityInterceptor(), FilterSecurityInterceptor.class)
        ;
    }
}
