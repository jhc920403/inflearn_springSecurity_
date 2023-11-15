package com.core.corespringsecurity.controller.admin;

import com.core.corespringsecurity.domain.dto.AccountDto;
import com.core.corespringsecurity.service.RoleService;
import com.core.corespringsecurity.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.websocket.server.PathParam;

@Controller
@RequiredArgsConstructor
public class UserManagerController {

    private final UserService userService;
    private final RoleService roleService;

    @GetMapping("/admin/accounts")
    public String getUsers(Model model) {
        model.addAttribute("accounts", userService.getUsers());
        return "admin/user/list";
    }

    @PostMapping("/admin/accounts")
    public String modifyUser(AccountDto accountDto) {
        userService.modifyUser(accountDto);
        return "redirect:/admin/accounts";
    }

    @GetMapping("/admin/accounts/{id}")
    public String getUser(@PathVariable(value = "id")Long id, Model model) {
        model.addAttribute("account", userService.getUser(id));
        model.addAttribute("roleList", roleService.getRoles());
        return "admin/user/detail";
    }

    @GetMapping(value = "/admin/accounts/delete/{id}")
    public String removeUser(@PathVariable(value = "id")Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }
}
