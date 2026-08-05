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

    @GetMapping("/zones")
    public String zones() {
        return "redirect:/zones.html";
    }

    @GetMapping("/alerts")
    public String alerts() {
        return "redirect:/alerts.html";
    }

    @GetMapping("/readings")
    public String readings() {
        return "redirect:/readings.html";
    }
}
