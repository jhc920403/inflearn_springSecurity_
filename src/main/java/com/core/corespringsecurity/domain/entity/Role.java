package com.core.corespringsecurity.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * CREATE TABLE "role" (
 * 	role_id int8 NOT NULL,
 * 	role_desc varchar(255) NULL,
 * 	role_name varchar(255) NULL,
 * 	CONSTRAINT role_pkey PRIMARY KEY (role_id)
 * );
 */
@Entity
@Table(name = "ROLE")
@ToString(exclude = {"users", "resourceSet"})
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Role {

    @Id @GeneratedValue
    @Column(name = "role_id")
    private Long id;

    @Column(name = "role_name")
    private String roleName;

    @Column(name = "role_desc")
    private String roleDesc;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "roleSet")
    @OrderBy("orderNum desc")
    private Set<Resource> resourceSet = new LinkedHashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "userRoles")
    private Set<Account> accounts = new HashSet<>();
}
