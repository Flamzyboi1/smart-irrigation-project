package lv.venta.irrigation.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lv.venta.irrigation.model.IrrigationAlert;
import lv.venta.irrigation.model.IrrigationZone;
import lv.venta.irrigation.model.SensorReading;
import lv.venta.irrigation.service.IrrigationService;

@RestController
@RequestMapping("/api")
public class IrrigationController {

    @Autowired
    private IrrigationService service;

    @GetMapping("/dashboard")
    public List<Map<String, Object>> getDashboard() {
        return service.getAllSensorsWithLatestReading();
    }

    @GetMapping("/sensors")
    public List<Map<String, Object>> getSensors() {
        return service.getAllSensorsWithLatestReading();
    }

    @GetMapping("/zones")
    public Iterable<IrrigationZone> getZones() {
        return service.getAllZones();
    }

    @GetMapping("/alerts")
    public Iterable<IrrigationAlert> getAlerts() {
        return service.getAllAlerts();
    }

    @GetMapping("/alerts/active")
    public Iterable<IrrigationAlert> getActiveAlerts() {
        return service.getActiveAlerts();
    }

    @GetMapping("/readings")
    public List<Map<String, Object>> getReadings() {
        return service.getAllReadings();
    }

    @PostMapping("/readings")
    public SensorReading postReading(@RequestBody Map<String, Object> body) {
        String sensorId = (String) body.get("sensorId");
        double moisture = Double.parseDouble(body.get("soilMoisture").toString());
        double temperature = Double.parseDouble(body.get("temperature").toString());
        double rainfall = body.containsKey("rainfall")
            ? Double.parseDouble(body.get("rainfall").toString()) : 0.0;
        double flowRate = body.containsKey("flowRate")
            ? Double.parseDouble(body.get("flowRate").toString()) : 0.5;
        return service.saveReading(sensorId, moisture, temperature, rainfall, flowRate);
    }

    @PostMapping("/zones/{id}/toggle-valve")
    public void toggleValve(@PathVariable Long id) {
        service.toggleValve(id);
    }

    @PostMapping("/alerts/{id}/acknowledge")
    public void acknowledgeAlert(@PathVariable Long id) {
        service.acknowledgeAlert(id);
    }
}