package com.core.corespringsecurity.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MessageController {

    @GetMapping("/messages")
    public String myPage() {
        return "/user/messages";
    }

    @ResponseBody
    @GetMapping("/api/messages")
    public String apiMessages() {
        return "messages ok";
    }
}
