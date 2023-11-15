package com.core.corespringsecurity.security.config;

import com.core.corespringsecurity.security.common.AjaxLoginAuthenticationEntryPoint;
import com.core.corespringsecurity.security.handler.AjaxAccessDeniedHandler;
import com.core.corespringsecurity.security.provider.AjaxAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * Ajax 비동기 인증 처리 흐름
 * 1) AjaxLoginProcessingFilter에서 인증정보를 담는다.
 * 2) AjaxLoginProcessingFilter의 setAuthenticationManager 메소드를 사용하여 Manager에 전달
 * 3) AuthenticationManager에서 authenticationProvider 메소드를 사용하여 Provider에게 인증처리 위임
 */
@Order(0)
@Configuration
@RequiredArgsConstructor
public class AjaxSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationSuccessHandler ajaxAuthenticationSuccessHandler;
    private final AuthenticationFailureHandler ajaxAuthenticationFailureHandler;

    /* Custom Lambda DSL을 통해 기존 non-Lambda DSL은 주석처리 진행
    // 1) AjaxLoginProcessingFilter에서 인증정보를 담는다.
    @Bean
    public AjaxLoginProcessingFilter ajaxLoginProcessingFilter() throws Exception {
        AjaxLoginProcessingFilter ajaxLoginProcessingFilter = new AjaxLoginProcessingFilter();
        ajaxLoginProcessingFilter.setAuthenticationManager(authenticationManagerBean());
        ajaxLoginProcessingFilter.setAuthenticationSuccessHandler(ajaxAuthenticationSuccessHandler);
        ajaxLoginProcessingFilter.setAuthenticationFailureHandler(ajaxAuthenticationFailureHandler);

        return ajaxLoginProcessingFilter;
    }

    // 2) AjaxLoginProcessingFilter의 setAuthenticationManager 메소드를 사용하여 Manager에 전달
    */

    // 3) AuthenticationManager에서 authenticationProvider 메소드를 사용하여 Provider에게 인증처리 위임
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(ajaxAuthenticationProvider());
    }

    @Bean
    public AuthenticationProvider ajaxAuthenticationProvider() {
        return new AjaxAuthenticationProvider(userDetailsService, passwordEncoder);
    }


    @Bean
    public AccessDeniedHandler ajaxAccessDeniedHandler() {
       return new AjaxAccessDeniedHandler();
    }

    /**
     * ajax 비동기 형태의 인증방식으로 구현한 SecurityFilterChain
     * (non-lambda DSL 형태의 configurer 코드이다.)
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        /* Custom Lambda DSL을 통해 기존 non-Lambda DSL은 주석처리 진행
        http
                .antMatcher("/api/**")
                .authorizeRequests()
                .antMatchers("/api/login").permitAll()
                .antMatchers("/api/messages").hasRole("MANAGER")
                .anyRequest().authenticated()
        .and()
                .addFilterBefore(ajaxLoginProcessingFilter(), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(new AjaxLoginAuthenticationEntryPoint())  // 인증이 필요한 경우
                .accessDeniedHandler(ajaxAccessDeniedHandler())                     // 인증은 됐지만 인가가 안된 경우
        .and()
                .csrf().disable()
        ;
        */

        http
                .antMatcher("/api/**")
                .authorizeRequests()
                .antMatchers("/api/login").permitAll()
                .antMatchers("/api/messages").hasRole("MANAGER")
                .anyRequest().authenticated()
            .and()
                .exceptionHandling()
                .authenticationEntryPoint(new AjaxLoginAuthenticationEntryPoint())
                .accessDeniedHandler(ajaxAccessDeniedHandler())
        ;

        getCustomConfigurerAjax(http);
    }

    private void getCustomConfigurerAjax(HttpSecurity http) throws Exception {
        http
                .apply(new AjaxLoginConfigurer<>())
                .successHandlerAjax(ajaxAuthenticationSuccessHandler)
                .failureHandlerAjax(ajaxAuthenticationFailureHandler)
                .setAuthenticationManager(authenticationManagerBean())
                .loginProcessingUrl("/api/login")
                .loginPage("/api/login")
        ;
    }
}
