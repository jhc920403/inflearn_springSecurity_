package com.core.corespringsecurity.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * CREATE TABLE role_hierarchy (
 * 	id int8 NOT NULL,
 * 	child_name varchar(255) NULL,
 * 	parent_name varchar(255) NULL,
 * 	CONSTRAINT role_hierarchy_pkey PRIMARY KEY (id),
 * 	CONSTRAINT uk_aux81x2bb3geojtq6mf8rs19l UNIQUE (child_name),
 * 	CONSTRAINT fk7nx52tgar4fm7tk54jil95oxb FOREIGN KEY (parent_name) REFERENCES public.role_hierarchy(child_name)
 * );
 */
@Entity
@Table(name = "ROLE_HIERARCHY")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RoleHierarchy implements Serializable {

    @Id @GeneratedValue
    private Long id;

    @Column(name = "child_name")
    private String childName;

    @JoinColumn(name = "parent_name", referencedColumnName = "child_name")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private RoleHierarchy parentName;

    @OneToMany(mappedBy = "parentName", cascade = CascadeType.ALL)
    private Set<RoleHierarchy> roleHierarchy = new HashSet<RoleHierarchy>();
}
