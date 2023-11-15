package com.core.corespringsecurity.security.service;

import com.core.corespringsecurity.domain.entity.Account;
import com.core.corespringsecurity.domain.entity.Role;
import com.core.corespringsecurity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Spring Security에서 인증 진행할 때 참조하여, 사용자에게 맞는 권한을 UserDetails에 저장한다.
 * * UserDetailsService는 조회된 사용자 정보를 Spring Security가 사용할 수 있는 형태로 수정한다.
 */
@Slf4j
@Service("userDetailsService")
@Transactional
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Account account = userRepository.findByUsername(username);

        if (account == null) {
            throw new UsernameNotFoundException("UsernameNotFoundException");
        }

        /*
         * * 권한을 담을 List를 추가한다.
         * GrantedAuthority는 Interface이며, List의 데이터 형태로 GrantedAuthority를 구현한 SimpleGrantedAuthority 객체를 담아준다.
         */
        // List<GrantedAuthority> roles = new ArrayList<>();
        // roles.add(new SimpleGrantedAuthority(account.getRole()));

        Set<String> userRoles = account.getUserRoles()
                .stream()
                .map(userRole -> userRole.getRoleName())
                .collect(Collectors.toSet());

        List<GrantedAuthority> collect = userRoles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        return new AccountContext(account, collect);
    }
}
