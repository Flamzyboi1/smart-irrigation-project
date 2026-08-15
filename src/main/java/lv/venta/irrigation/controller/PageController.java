package lv.venta.irrigation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    @GetMapping({"/", "/index.html"}) public String home(){return "redirect:/login.html";}
    @GetMapping("/field-blocks") public String fieldBlocks(){return "redirect:/field-blocks.html";}
}
