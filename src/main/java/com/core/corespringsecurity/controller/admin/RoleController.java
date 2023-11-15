package com.core.corespringsecurity.controller.admin;

import com.core.corespringsecurity.domain.dto.RoleDto;
import com.core.corespringsecurity.domain.entity.Role;
import com.core.corespringsecurity.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping("/admin/roles")
    public String getRoles(Model model) {
        model.addAttribute("roles", roleService.getRoles());
        return "admin/role/list";
    }

    @GetMapping("/admin/roles/register")
    public String viewRoles(Model model) {
        model.addAttribute("role", new RoleDto());
        return "admin/role/detail";
    }

    @PostMapping("/admin/roles")
    public String createRole(RoleDto roleDto) {
        ModelMapper modelMapper = new ModelMapper();
        Role role = modelMapper.map(roleDto, Role.class);
        roleService.createRole(role);

        return "redirect:/admin/roles";
    }

    @GetMapping("/admin/roles/{id}")
    public String getRole(@PathVariable("id") Long id, Model model) {
        ModelMapper modelMapper = new ModelMapper();
        model.addAttribute("role", modelMapper.map(roleService.getRole(id), RoleDto.class));
        return "/admin/role/detail";
    }

    @GetMapping("/admin/roles/delete/{id}")
    public String removeResources(@PathVariable("id") Long id, Model model) {
        roleService.deleteRole(id);
        return "redirect:/admin/resources";
    }
}
