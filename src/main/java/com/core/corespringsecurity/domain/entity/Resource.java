package com.core.corespringsecurity.domain.entity;

import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Resource Entity :
 *
 * CREATE TABLE resources (
 * 	resource_id int8 NOT NULL,
 * 	http_method varchar(255) NULL,
 * 	order_num int4 NULL,
 * 	resource_name varchar(255) NULL,
 * 	resource_type varchar(255) NULL,
 * 	CONSTRAINT resources_pkey PRIMARY KEY (resource_id)
 * );
 */
@Data
@Entity
@Table(name = "RESOURCES")
@ToString(exclude = "roleSet")
@EntityListeners(value = {AuditingEntityListener.class})
@EqualsAndHashCode(of = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Resource {

    @Id
    @GeneratedValue
    @Column(name = "resource_id")
    private Long id;

    @Column(name = "resource_name")
    private String resourceName;

    @Column(name = "http_method")
    private String httpMethod;

    @Column(name = "order_num")
    private Integer orderNum;

    @Column(name = "resource_type")
    private String resourceType;

    /**
     * CREATE TABLE role_resource (
     * 	resource_id int8 NOT NULL,
     * 	role_id int8 NOT NULL,
     * 	CONSTRAINT role_resource_pkey PRIMARY KEY (resource_id, role_id),
     * 	CONSTRAINT fk9n7etkb3eqnlbni7k9sj3eiel FOREIGN KEY (resource_id) REFERENCES public.resources(resource_id),
     * 	CONSTRAINT fkh8lunkrwoyio367ec8y12bis1 FOREIGN KEY (role_id) REFERENCES public."role"(role_id)
     * );
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "role_resource"
            , joinColumns = @JoinColumn(name = "resource_id")
            , inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roleSet = new HashSet<>();
}
