package lv.venta.irrigation.controller;

import lv.venta.irrigation.service.SensLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/senslog")
public class SensLogController {
    private final SensLogService service;
    public SensLogController(SensLogService service){this.service=service;}
    @GetMapping("/units") public ResponseEntity<String> units(){return service.units();}
    @GetMapping("/latest") public ResponseEntity<String> latest(){return service.latest();}
    @GetMapping("/observations") public ResponseEntity<String> observations(@RequestParam(required=false) Long unitId,@RequestParam(required=false) Long sensorId,@RequestParam(required=false) String fromTime,@RequestParam(required=false) String toTime){return service.observations(unitId,sensorId,fromTime,toTime);}
    @PostMapping("/observation") public ResponseEntity<String> insert(@RequestBody Map<String,Object> payload){return service.insert(payload);}
}
