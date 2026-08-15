package lv.venta.irrigation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    @GetMapping("/") public String root(){return "forward:/index.html";}
    @GetMapping("/dashboard") public String dashboard(){return "forward:/dashboard.html";}
    @GetMapping("/field-blocks") public String fields(){return "forward:/field-blocks.html";}
}
