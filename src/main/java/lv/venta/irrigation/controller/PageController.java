package lv.venta.irrigation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    @GetMapping("/dashboard")
    public String dashboard() { return "forward:/dashboard.html"; }

    @GetMapping("/field-blocks")
    public String fieldBlocks() { return "forward:/field-blocks.html"; }
}
