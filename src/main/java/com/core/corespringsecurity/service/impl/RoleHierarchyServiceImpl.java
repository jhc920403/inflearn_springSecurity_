package com.core.corespringsecurity.service.impl;

import com.core.corespringsecurity.domain.entity.RoleHierarchy;
import com.core.corespringsecurity.repository.RoleHierarchyRepository;
import com.core.corespringsecurity.service.RoleHierarchyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleHierarchyServiceImpl implements RoleHierarchyService {

    private final RoleHierarchyRepository roleHierarchyRepository;

    @Transactional
    @Override
    public String findAllHierarchy() {

        List<RoleHierarchy> rolesHierarchy = roleHierarchyRepository.findAll();

        Iterator<RoleHierarchy> roleHierarchyIterator = rolesHierarchy.iterator();
        StringBuilder concatedRoles = new StringBuilder();

        while (roleHierarchyIterator.hasNext()) {
            RoleHierarchy roleHierarchy = roleHierarchyIterator.next();

            if (roleHierarchy.getParentName() != null) {
                concatedRoles.append(roleHierarchy.getParentName().getChildName());
                concatedRoles.append(" > ");
                concatedRoles.append(roleHierarchy.getChildName());
                concatedRoles.append("\n");
            }
        }

        return concatedRoles.toString();
    }
}
