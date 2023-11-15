package com.core.corespringsecurity.service.impl;

import com.core.corespringsecurity.domain.entity.Role;
import com.core.corespringsecurity.repository.RoleRepository;
import com.core.corespringsecurity.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public Role getRole(Long id) {
        return roleRepository.findById(id).orElseGet(Role::new);
    }

    @Override
    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    @Override
    @Transactional(readOnly = false)
    public void createRole(Role role) {
        roleRepository.save(role);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
    }
}
