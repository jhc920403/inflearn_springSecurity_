package com.core.corespringsecurity.security.provider;

import com.core.corespringsecurity.security.common.FormWebAuthenticationDetails;
import com.core.corespringsecurity.security.service.AccountContext;
import com.core.corespringsecurity.security.token.AjaxAuthenticationToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@RequiredArgsConstructor
public class AjaxAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    /**
     * 실질적인 검증 처리 구현한다.
     * ProviderManager.java를 살펴보면 동작을 확인할 수 있다.
     * @param authentication authenticationManager로부터 제공받은 객체 파라미터이다. (사용자가 입력한 ID / PW 정보가 저장되어있다.)
     * @return
     * @throws AuthenticationException
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String username = authentication.getName();
        String password = (String) authentication.getCredentials();

        AccountContext accountContext = (AccountContext) userDetailsService.loadUserByUsername(username);

        log.info("Input Password :: {}\nDatabase Password :: {}\nmatch :: {}\nSecurityKey :: {}"
                , passwordEncoder.encode(password)
                , accountContext.getPassword()
                , passwordEncoder.matches(password, accountContext.getAccount().getPassword())
                //, ((FormWebAuthenticationDetails) authentication.getDetails()).getSecurityKey()
        );

        if(!passwordEncoder.matches(password, accountContext.getAccount().getPassword())){
            throw new BadCredentialsException("BadCredentialsException");
        }

        return new AjaxAuthenticationToken(
                accountContext.getAccount()
                , accountContext.getPassword()
                , accountContext.getAuthorities()
        );
    }

    /**
     * 인증처리 기준을 정의한다.
     * 파라미터로 받는 authentication와 CustomAuthenticationProvider에서 사용하는 토큰이 일치 여부를 검증
     * @param authentication
     * @return
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return AjaxAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
