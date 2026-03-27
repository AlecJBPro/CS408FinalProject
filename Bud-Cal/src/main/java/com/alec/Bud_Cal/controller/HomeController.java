package com.alec.Bud_Cal.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(HttpSession session) {
        return session.getAttribute("userEmail") == null ? "redirect:/login" : "redirect:/dashboard";
    }
}
