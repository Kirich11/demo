package com.presentation.http;

import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;

@Controller
public class IndexController implements ErrorController {

    @RequestMapping(value = "/error")
    public String error() {
        return "Error handling";
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }
}