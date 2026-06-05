package com.presentation.http;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/about")
public class AboutController {
    @Value("${spring.application.name}")
    String appName;

    @GetMapping
    public String about(Model model) {
        model.addAttribute("appName", appName);
        return "about";
    }
}
