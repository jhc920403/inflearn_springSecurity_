package com.core.corespringsecurity.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

/**
 * CREATE TABLE access_ip (
 * 	ip_id int8 NOT NULL,
 * 	ip_address varchar(255) NOT NULL,
 * 	CONSTRAINT access_ip_pkey PRIMARY KEY (ip_id)
 * );
 */
@Entity
@Table(name = "ACCESS_IP")
@Data
@EqualsAndHashCode(of = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessIp implements Serializable {

    @Id @GeneratedValue
    @Column(name = "IP_ID", unique = true, nullable = false)
    private Long id;

    @Column(name = "IP_ADDRESS", nullable = false)
    private String ipAddress;
}
