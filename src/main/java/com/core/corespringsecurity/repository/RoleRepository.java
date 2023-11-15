package com.core.corespringsecurity.repository;

import com.core.corespringsecurity.domain.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByRoleName(String roleName);
    Optional<Role> findById(Long id);
}
