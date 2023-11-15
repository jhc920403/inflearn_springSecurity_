package com.core.corespringsecurity.domain.entity;

import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * account Entity : 사용자 인증 계정 정보를 저장
 *
 * CREATE TABLE account (
 * 	id int8 NOT NULL,
 * 	age varchar(255) NULL,
 * 	email varchar(255) NULL,
 * 	"password" varchar(255) NULL,
 * 	"role" varchar(255) NULL,
 * 	username varchar(255) NULL,
 * 	CONSTRAINT account_pkey PRIMARY KEY (id)
 * );
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "age")
    private String age;

    @Column(name = "role")
    private String role;

    /**
     * CREATE TABLE account_roles (
     * 	account_id int8 NOT NULL,
     * 	role_id int8 NOT NULL,
     * 	CONSTRAINT account_roles_pkey PRIMARY KEY (account_id, role_id),
     * 	CONSTRAINT fki84870gssnbi37wfqfifekghb FOREIGN KEY (role_id) REFERENCES public."role"(role_id),
     * 	CONSTRAINT fktp61eta5i06bug3w1qr6286uf FOREIGN KEY (account_id) REFERENCES public.account(id)
     * );
     */
    @ManyToMany(fetch = FetchType.LAZY, cascade={CascadeType.ALL})
    @JoinTable(name = "account_roles", joinColumns = { @JoinColumn(name = "account_id") }, inverseJoinColumns = {
            @JoinColumn(name = "role_id") })
    private Set<Role> userRoles = new HashSet<>();

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Account account = (Account) o;
        return getId() != null && Objects.equals(getId(), account.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
