package lv.venta.irrigation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/")
    public String index() {
        return "redirect:/index.html";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "redirect:/index.html";
    }

    @GetMapping("/login")
    public String login() {
        return "redirect:/login.html";
    }

    @GetMapping("/users")
    public String users() {
        return "redirect:/user-management.html";
    }

    @GetMapping("/user-management")
    public String userManagement() {
        return "redirect:/user-management.html";
    }
}
