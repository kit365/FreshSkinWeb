package com.kit.maximus.freshskinweb.controller.home;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }
    @GetMapping("/user-home")
    public String home() {
        return "home";
    }

    @GetMapping("/user-info")
    @ResponseBody
    public String getUserInfo(@AuthenticationPrincipal OAuth2User principal) {
        if (principal != null) {
            return "Xin chào " + principal.getAttribute("name") +
                    "\nEmail: " + principal.getAttribute("email");
        }
        return "Chưa đăng nhập";
    }
}