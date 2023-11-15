package com.core.corespringsecurity.service;

import com.core.corespringsecurity.domain.entity.Role;

import java.util.List;

public interface RoleService {

    public Role getRole(Long id);
    public List<Role> getRoles();
    public void createRole(Role role);
    public void deleteRole(Long id);
}
