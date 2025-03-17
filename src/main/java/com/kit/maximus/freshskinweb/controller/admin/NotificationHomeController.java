package com.kit.maximus.freshskinweb.controller.admin;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/notify")
@Slf4j
public class NotificationHomeController {

    @GetMapping("home")
    public String home() {
        return "notification.html";
    }
}
