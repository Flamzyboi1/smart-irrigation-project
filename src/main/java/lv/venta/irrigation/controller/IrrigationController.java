package lv.venta.irrigation.controller;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import lv.venta.irrigation.model.*;
import lv.venta.irrigation.service.IrrigationService;
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class IrrigationController {
 @Autowired private IrrigationService service;
 @GetMapping("/dashboard") public List<Map<String,Object>> getDashboard(){return service.getAllSensorsWithLatestReading();}
 @GetMapping("/sensors") public List<Map<String,Object>> getSensors(){return service.getAllSensorsWithLatestReading();}
 @GetMapping("/zones") public Iterable<IrrigationZone> getZones(){return service.getAllZones();}
 @GetMapping("/alerts") public Iterable<IrrigationAlert> getAlerts(){return service.getAllAlerts();}
 @GetMapping("/alerts/active") public Iterable<IrrigationAlert> getActiveAlerts(){return service.getActiveAlerts();}
 @GetMapping("/readings") public List<Map<String,Object>> getReadings(){return service.getAllReadings();}
 @PostMapping("/readings")
 public ResponseEntity<?> postReading(@RequestBody Map<String,Object> body){
 try{
 String sensorId=String.valueOf(body.get("sensorId"));
 SensorReading saved=service.saveReading(sensorId,n(body,"soilPercent"),n(body,"temperature"),n(body,"humidity"),n(body,"rainfall"),n(body,"flowRate"),n(body,"batteryVoltage"));
 return ResponseEntity.ok(saved);
 }catch(Exception e){return ResponseEntity.badRequest().body(Map.of("error",e.getMessage()));}
 }
 @PostMapping("/zones/{id}/toggle-valve") public void toggleValve(@PathVariable Long id){service.toggleValve(id);}
 @PostMapping("/alerts/{id}/acknowledge") public void acknowledgeAlert(@PathVariable Long id){service.acknowledgeAlert(id);}
 @PostMapping("/zones/{id}/crop")
 public ResponseEntity<?> updateCrop(@PathVariable Long id,@RequestBody Map<String,Object> body){
 try{
 String cropType=String.valueOf(body.get("cropType"));
 IrrigationZone updated=service.updateCropType(id,cropType);
 return ResponseEntity.ok(updated);
 }catch(Exception e){return ResponseEntity.badRequest().body(Map.of("error",e.getMessage()));}
 }
 @PostMapping("/zones/{id}/settings")
 public ResponseEntity<?> updateZoneSettings(@PathVariable Long id,@RequestBody Map<String,Object> body){
 try{
 String cropType=body.get("cropType")!=null?String.valueOf(body.get("cropType")):null;
 double moistureThreshold=body.get("moistureThreshold")!=null?Double.parseDouble(String.valueOf(body.get("moistureThreshold"))):0;
 double maxTemperature=body.get("maxTemperature")!=null?Double.parseDouble(String.valueOf(body.get("maxTemperature"))):0;
 IrrigationZone updated=service.updateZoneSettings(id,cropType,moistureThreshold,maxTemperature);
 return ResponseEntity.ok(updated);
 }catch(Exception e){return ResponseEntity.badRequest().body(Map.of("error",e.getMessage()));}
 }
 private double n(Map<String,Object> body,String key){Object v=body.get(key);return v==null?0.0:Double.parseDouble(v.toString());}
}
