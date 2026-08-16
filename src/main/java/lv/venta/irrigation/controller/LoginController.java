package lv.venta.irrigation.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {
    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, HttpSession session) {
        if ("EcoigmAdmin".equals(username) && "Ecoigm123#".equals(password)) {
            session.setAttribute("authenticatedUser", username);
            return "redirect:/dashboard.html";
        }
        return "redirect:/login.html?error=1";
    }
}
