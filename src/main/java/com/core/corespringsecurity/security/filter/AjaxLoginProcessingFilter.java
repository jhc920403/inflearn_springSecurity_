package com.core.corespringsecurity.security.filter;

import com.core.corespringsecurity.domain.dto.AccountDto;
import com.core.corespringsecurity.security.token.AjaxAuthenticationToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Ajax 방식으로 요청이 들어온 경우 인증 수행하게 되는 클래스이다.
 */
@Slf4j
public class AjaxLoginProcessingFilter extends AbstractAuthenticationProcessingFilter {

    // 인증정보를 Json 형태로 받을 것이며, AccountDto로 저장하는 매커니즘으로 동작한다.
    private ObjectMapper objectMapper = new ObjectMapper();

    public AjaxLoginProcessingFilter() {
        /**
         * new AntPathRequestMatcher("/api/login") : 해당 Url로 들어온 경우 필터가 동작된다.
         */
        super(new AntPathRequestMatcher("/api/login", "POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {

        if(!isAjax(request)) {
            throw new IllegalStateException("Authentication is not supported");
        }

        AccountDto accountDto = objectMapper.readValue(request.getReader(), AccountDto.class);
        if(accountDto.getUsername().isBlank() || accountDto.getPassword().isBlank()) {
            throw new IllegalArgumentException("Username or Password is empty");
        }

        log.debug("\n\taccountDto.getUsername :: {}\n\taccountDto.getPassword :: {}", accountDto.getUsername(), accountDto.getPassword());
        AjaxAuthenticationToken ajaxAuthenticationToken = new AjaxAuthenticationToken(accountDto.getUsername(), accountDto.getPassword());

        // Manager에게 전달한 인증정보로 인증 성공시 반환받는다.
        return getAuthenticationManager().authenticate(ajaxAuthenticationToken);
    }

    private boolean isAjax(HttpServletRequest request) {
        /**
         * 요청을 받을 때 클라이언트와 서버간 정의한 Key Value 값을 통해 확인할 수 있도록 작성되어있다.
         */
        if("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return true;
        }

        return false;
    }
}
