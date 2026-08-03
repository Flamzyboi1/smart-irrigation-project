package lv.venta.irrigation.controller;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lv.venta.irrigation.model.IrrigationZone;
import lv.venta.irrigation.model.Sensor;
import lv.venta.irrigation.model.SensorReading;
import lv.venta.irrigation.repo.SensorCrudRepository;
import lv.venta.irrigation.repo.SensorReadingCrudRepository;
import lv.venta.irrigation.repo.ZoneCrudRepository;

@RestController
@RequestMapping("/api/readings")
@CrossOrigin(origins = "*")
public class SensorPushController {

    @Autowired
    private SensorReadingCrudRepository readingRepo;

    @Autowired
    private SensorCrudRepository sensorRepo;

    @Autowired
    private ZoneCrudRepository zoneRepo;

    @PostMapping("/push")
    public ResponseEntity<?> pushReading(@RequestBody Map<String, Object> payload) {

        if (!payload.containsKey("sensorId") || payload.get("sensorId") == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Missing required field: sensorId"));
        }

        String sensorId = payload.get("sensorId").toString();
        Sensor sensor = sensorRepo.findBySensorId(sensorId);

        if (sensor == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "No sensor found with sensorId: " + sensorId
            ));
        }

        SensorReading reading = new SensorReading();
        reading.setSensor(sensor);
        reading.setTimestamp(LocalDateTime.now());

        double soilPercent = toDouble(payload.getOrDefault("soilPercent", 0.0));
        double temperature = toDouble(payload.getOrDefault("temperature", 0.0));
        double humidity = toDouble(payload.getOrDefault("humidity", 0.0));
        double rainfall = toDouble(payload.getOrDefault("rainfall", 0.0));
        double flowRate = toDouble(payload.getOrDefault("flowRate", 0.0));
        double batteryVoltage = toDouble(payload.getOrDefault("batteryVoltage", 0.0));

        IrrigationZone zone = sensor.getZone();
        double threshold = resolveCropThreshold(zone);
        double vwc = calculateVwc(soilPercent, zone);

        reading.setSoilPercent(soilPercent);
        reading.setVolumetricWaterContent(vwc);
        reading.setTemperature(temperature);
        reading.setHumidity(humidity);
        reading.setRainfall(rainfall);
        reading.setFlowRate(flowRate);
        reading.setBatteryVoltage(batteryVoltage);

        if (vwc < threshold - 10) {
            reading.setRecommendation("IRRIGATE_NOW");
            if (zone != null) {
                zone.setStatus("ALERT");
                zone.setValveOpen(true);
                zoneRepo.save(zone);
            }
        } else if (vwc < threshold) {
            reading.setRecommendation("IRRIGATE");
            if (zone != null) {
                zone.setStatus("DRY");
                zone.setValveOpen(false);
                zoneRepo.save(zone);
            }
        } else if (vwc > 80) {
            reading.setRecommendation("EXCESS_WATER");
            if (zone != null) {
                zone.setStatus("WET");
                zone.setValveOpen(false);
                zoneRepo.save(zone);
            }
        } else {
            reading.setRecommendation("ADEQUATE");
            if (zone != null) {
                zone.setStatus("NORMAL");
                zone.setValveOpen(false);
                zoneRepo.save(zone);
            }
        }

        readingRepo.save(reading);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "saved");
        response.put("sensorId", sensorId);
        response.put("cropType", zone != null ? zone.getCropType() : "Unknown");
        response.put("soilPercent", reading.getSoilPercent());
        response.put("volumetricWaterContent", reading.getVolumetricWaterContent());
        response.put("temperature", reading.getTemperature());
        response.put("humidity", reading.getHumidity());
        response.put("rainfall", reading.getRainfall());
        response.put("flowRate", reading.getFlowRate());
        response.put("batteryVoltage", reading.getBatteryVoltage());
        response.put("recommendation", reading.getRecommendation());
        response.put("thresholdUsed", threshold);
        response.put("timestamp", reading.getTimestamp().toString());

        if (zone != null) {
            response.put("zone", zone.getName());
            response.put("zoneStatus", zone.getStatus());
            response.put("valveOpen", zone.isValveOpen());
        }

        return ResponseEntity.ok(response);
    }

    private double resolveCropThreshold(IrrigationZone zone) {
        if (zone == null) return 25.0;

        String crop = zone.getCropType();
        if (crop == null || crop.isBlank()) {
            return zone.getMoistureThreshold() > 0 ? zone.getMoistureThreshold() : 25.0;
        }

        switch (crop.trim().toLowerCase()) {
            case "tomato":
            case "tomatoes":
                return 30.0;
            case "potato":
            case "potatoes":
                return 22.0;
            case "wheat":
                return 20.0;
            case "lettuce":
                return 35.0;
            default:
                return 25.0;
        }
    }

    private double calculateVwc(double soilPercent, IrrigationZone zone) {
        if (zone == null || zone.getCropType() == null) {
            return soilPercent;
        }

        String crop = zone.getCropType().trim().toLowerCase();

        switch (crop) {
            case "tomato":
            case "tomatoes":
                return soilPercent * 0.95;
            case "potato":
            case "potatoes":
                return soilPercent * 0.75;
            case "lettuce":
                return soilPercent * 1.05;
            case "wheat":
                return soilPercent * 0.70;
            default:
                return soilPercent * 0.85;
        }
    }

    private double toDouble(Object val) {
        if (val == null) return 0.0;
        if (val instanceof Number) return ((Number) val).doubleValue();
        try {
            return Double.parseDouble(val.toString());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}