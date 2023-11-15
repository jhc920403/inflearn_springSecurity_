package com.core.corespringsecurity.security.service;

import com.core.corespringsecurity.domain.entity.Account;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * 유저의 인증 정보를 담는 UserDetails 구현한 User를 상속받아 사용할 수 있다.
 * * Custom 필요시 UserDetails Interface를 구현하여 사용할 수 있다.
 */
public class AccountContext extends User {
    private final Account account;

    /**
     * AccountContext는 기본적으로 파라미터 정보가 username, password, authorities가 주어지며, Account로 username, password를 대체한다.
     * @param account : 인증 계정 정보를 담은 객체
     * @param authorities : 인증된 계정의 권한을 담은 객체
     */
    public AccountContext(Account account, Collection<? extends GrantedAuthority> authorities) {
        super(account.getUsername(), account.getPassword(), authorities);
        this.account = account;
    }

    public Account getAccount() {
        return account;
    }
}
