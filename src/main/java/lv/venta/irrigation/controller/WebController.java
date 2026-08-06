// Navbar v2: Users links updated to user-management.html
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

    @GetMapping("/users")
    public String users() {
        return "redirect:/users.html";
    }

    @GetMapping("/sensors")
    public String sensors() {
        return "redirect:/sensors.html";
    }
}
