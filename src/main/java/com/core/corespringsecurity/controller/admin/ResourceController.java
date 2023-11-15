package com.core.corespringsecurity.controller.admin;

import com.core.corespringsecurity.domain.dto.ResourceDto;
import com.core.corespringsecurity.domain.entity.Resource;
import com.core.corespringsecurity.domain.entity.Role;
import com.core.corespringsecurity.repository.RoleRepository;
import com.core.corespringsecurity.security.metadatasource.UrlFilterInvocationSecurityMetadataSource;
import com.core.corespringsecurity.service.MethodSecurityService;
import com.core.corespringsecurity.service.ResourceService;
import com.core.corespringsecurity.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;
    private final RoleRepository roleRepository;
    private final RoleService roleService;
    private final MethodSecurityService methodSecurityService;
    private final UrlFilterInvocationSecurityMetadataSource filterInvocationSecurityMetadataSource;

    @GetMapping("/admin/resources")
    public String getResource(Model model) {
        model.addAttribute("resources", resourceService.getResource());
        return "admin/resource/list";
    }

    @PostMapping("/admin/resources")
    public String createResource(ResourceDto resourceDto) throws Exception {
        ModelMapper modelMapper = new ModelMapper();
        Role role = roleRepository.findByRoleName(resourceDto.getRoleName());
        Set<Role> roles = new HashSet<>();
        roles.add(role);

        Resource resource = modelMapper.map(resourceDto, Resource.class);
        resource.setRoleSet(roles);

        resourceService.createResources(resource);

        if("url".equals(resourceDto.getResourceType())) {
            filterInvocationSecurityMetadataSource.reload();
        } else {
            methodSecurityService.addMethodSecured(resourceDto.getResourceName(), resourceDto.getRoleName());
        }

        return "redirect:/admin/resources";
    }

    @GetMapping("/admin/resources/register")
    public String viewRoles(Model model) {
        model.addAttribute("roleList", roleService.getRoles());

        ResourceDto resource = new ResourceDto();
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(new Role());
        resource.setRoleSet(roleSet);
        model.addAttribute("resources", resource);

        return "admin/resource/detail";
    }

    @GetMapping("/admin/resources/{id}")
    public String getResource(@PathVariable("id") Long id, Model model) throws Exception {
        model.addAttribute("roleList", roleService.getRoles());
        Resource resource = resourceService.getResource(id);

        ModelMapper modelMapper = new ModelMapper();
        model.addAttribute("resources",modelMapper.map(resource, ResourceDto.class));

        if("url".equals(resource.getResourceType())) {
            filterInvocationSecurityMetadataSource.reload();
        } else {
            methodSecurityService.removeMethodSecured(resource.getResourceName());
        }

        return "admin/resource/detail";
    }

    @GetMapping("/admin/resources/delete/{id}")
    public String removeResource(@PathVariable("id") Long id, Model model) throws Exception {

        Resource resource = resourceService.getResource(Long.valueOf(id));
        resourceService.deleteResources(Long.valueOf(id));

        if("url".equals(resource.getResourceType())) {
            filterInvocationSecurityMetadataSource.reload();
        }else{
            methodSecurityService.removeMethodSecured(resource.getResourceName());
        }

        return "redirect:/admin/resources";
    }
}
