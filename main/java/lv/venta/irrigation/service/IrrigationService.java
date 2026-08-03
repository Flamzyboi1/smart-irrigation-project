package lv.venta.irrigation.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lv.venta.irrigation.model.IrrigationAlert;
import lv.venta.irrigation.model.IrrigationZone;
import lv.venta.irrigation.model.Sensor;
import lv.venta.irrigation.model.SensorReading;
import lv.venta.irrigation.repo.AlertCrudRepository;
import lv.venta.irrigation.repo.SensorCrudRepository;
import lv.venta.irrigation.repo.SensorReadingCrudRepository;
import lv.venta.irrigation.repo.ZoneCrudRepository;

@Service
public class IrrigationService {

    @Autowired
    private SensorCrudRepository sensorRepo;

    @Autowired
    private SensorReadingCrudRepository readingRepo;

    @Autowired
    private ZoneCrudRepository zoneRepo;

    @Autowired
    private AlertCrudRepository alertRepo;

    public SensorReading saveReading(String sensorId,
                                     double soilPercent,
                                     double temperature,
                                     double humidity,
                                     double rainfall,
                                     double flowRate,
                                     double batteryVoltage) {

        Sensor sensor = sensorRepo.findBySensorId(sensorId);
        if (sensor == null) {
            throw new RuntimeException("Sensor not found: " + sensorId);
        }

        IrrigationZone zone = sensor.getZone();
        double threshold = resolveCropThreshold(zone);
        double volumetricWaterContent = calculateVwc(soilPercent, zone);

        SensorReading reading = new SensorReading();
        reading.setSensor(sensor);
        reading.setSoilPercent(soilPercent);
        reading.setVolumetricWaterContent(volumetricWaterContent);
        reading.setTemperature(temperature);
        reading.setHumidity(humidity);
        reading.setRainfall(rainfall);
        reading.setFlowRate(flowRate);
        reading.setBatteryVoltage(batteryVoltage);
        reading.setTimestamp(LocalDateTime.now());

        if (volumetricWaterContent < threshold - 10) {
            reading.setRecommendation("IRRIGATE_NOW");
            if (zone != null) {
                zone.setStatus("ALERT");
                zone.setValveOpen(true);
            }
            createAlert(sensorId, zone, "LOW_MOISTURE", "CRITICAL",
                    "CRITICAL: VWC at " + String.format("%.1f", volumetricWaterContent)
                            + "% far below threshold " + threshold + "%");

        } else if (volumetricWaterContent < threshold) {
            reading.setRecommendation("IRRIGATE");
            if (zone != null) {
                zone.setStatus("DRY");
                zone.setValveOpen(false);
            }
            createAlert(sensorId, zone, "LOW_MOISTURE", "WARNING",
                    "WARNING: VWC at " + String.format("%.1f", volumetricWaterContent)
                            + "% below threshold " + threshold + "%");

        } else if (volumetricWaterContent > 80) {
            reading.setRecommendation("EXCESS_WATER");
            if (zone != null) {
                zone.setStatus("WET");
                zone.setValveOpen(false);
            }
            createAlert(sensorId, zone, "HIGH_MOISTURE", "INFO",
                    "INFO: Excess moisture at " + String.format("%.1f", volumetricWaterContent) + "%");

        } else {
            reading.setRecommendation("ADEQUATE");
            if (zone != null) {
                zone.setStatus("NORMAL");
                zone.setValveOpen(false);
            }
        }

        if (temperature > 35.0) {
            createAlert(sensorId, zone, "HIGH_TEMP", "WARNING",
                    "WARNING: Temperature " + String.format("%.1f", temperature)
                            + "C exceeds safe limit");
        }

        if (zone != null) {
            zoneRepo.save(zone);
        }

        return readingRepo.save(reading);
    }

    public IrrigationZone updateCropType(Long zoneId, String cropType) {
        IrrigationZone zone = zoneRepo.findById(zoneId)
                .orElseThrow(() -> new RuntimeException("Zone not found: " + zoneId));

        String cleaned = cropType == null ? "" : cropType.trim();
        if (cleaned.isEmpty()) {
            throw new RuntimeException("Crop type cannot be empty");
        }

        zone.setCropType(cleaned);
        zone.setMoistureThreshold(getThresholdForCrop(cleaned));
        return zoneRepo.save(zone);
    }

    private double resolveCropThreshold(IrrigationZone zone) {
        if (zone == null) return 25.0;

        String crop = zone.getCropType();
        if (crop == null || crop.isBlank()) {
            return zone.getMoistureThreshold() > 0 ? zone.getMoistureThreshold() : 25.0;
        }

        return getThresholdForCrop(crop);
    }

    private double getThresholdForCrop(String crop) {
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

    private void createAlert(String sensorId,
                             IrrigationZone zone,
                             String type,
                             String severity,
                             String message) {
        IrrigationAlert alert = new IrrigationAlert();
        alert.setSensorId(sensorId);
        alert.setZoneName(zone != null ? zone.getName() : "Unknown");
        alert.setAlertType(type);
        alert.setSeverity(severity);
        alert.setMessage(message);
        alert.setAcknowledged(false);
        alert.setCreatedAt(LocalDateTime.now());
        alertRepo.save(alert);
    }

    public List<Map<String, Object>> getAllSensorsWithLatestReading() {
        List<Map<String, Object>> result = new ArrayList<>();

        for (Sensor sensor : sensorRepo.findAll()) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", sensor.getId());
            map.put("sensorId", sensor.getSensorId());
            map.put("type", sensor.getType());
            map.put("latitude", sensor.getLatitude());
            map.put("longitude", sensor.getLongitude());
            map.put("label", sensor.getLocationLabel());
            map.put("active", sensor.isActive());

            IrrigationZone zone = sensor.getZone();
            if (zone != null) {
                map.put("zoneName", zone.getName());
                map.put("zoneId", zone.getId());
                map.put("zoneStatus", zone.getStatus());
                map.put("valveOpen", zone.isValveOpen());
                map.put("threshold", resolveCropThreshold(zone));
                map.put("cropType", zone.getCropType());
            }

            List<SensorReading> readings = readingRepo.findTop10BySensorOrderByTimestampDesc(sensor);

            if (!readings.isEmpty()) {
                SensorReading latest = readings.get(0);
                map.put("soilPercent", latest.getSoilPercent());
                map.put("volumetricWaterContent", latest.getVolumetricWaterContent());
                map.put("temperature", latest.getTemperature());
                map.put("humidity", latest.getHumidity());
                map.put("rainfall", latest.getRainfall());
                map.put("flowRate", latest.getFlowRate());
                map.put("batteryVoltage", latest.getBatteryVoltage());
                map.put("recommendation", latest.getRecommendation());
                map.put("timestamp", latest.getTimestamp() != null ? latest.getTimestamp().toString() : "");

                List<Double> history = new ArrayList<>();
                for (SensorReading r : readings) {
                    history.add(r.getSoilPercent());
                }
                Collections.reverse(history);
                map.put("history", history);
            }

            result.add(map);
        }

        return result;
    }

    public List<Map<String, Object>> getAllReadings() {
        List<Map<String, Object>> result = new ArrayList<>();

        for (SensorReading r : readingRepo.findAll()) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", r.getId());
            map.put("sensorId", r.getSensor() != null ? r.getSensor().getSensorId() : "N/A");
            map.put("zoneName",
                    r.getSensor() != null && r.getSensor().getZone() != null
                            ? r.getSensor().getZone().getName()
                            : "N/A");
            map.put("soilPercent", r.getSoilPercent());
            map.put("volumetricWaterContent", r.getVolumetricWaterContent());
            map.put("temperature", r.getTemperature());
            map.put("humidity", r.getHumidity());
            map.put("rainfall", r.getRainfall());
            map.put("flowRate", r.getFlowRate());
            map.put("batteryVoltage", r.getBatteryVoltage());
            map.put("recommendation", r.getRecommendation());
            map.put("timestamp", r.getTimestamp() != null ? r.getTimestamp().toString() : "");
            result.add(map);
        }

        return result;
    }

    public Iterable<IrrigationZone> getAllZones() {
        return zoneRepo.findAll();
    }

    public Iterable<IrrigationAlert> getActiveAlerts() {
        return alertRepo.findByAcknowledgedFalse();
    }

    public Iterable<IrrigationAlert> getAllAlerts() {
        return alertRepo.findAll();
    }

    public void toggleValve(Long zoneId) {
        zoneRepo.findById(zoneId).ifPresent(zone -> {
            zone.setValveOpen(!zone.isValveOpen());
            zoneRepo.save(zone);
        });
    }

    public void acknowledgeAlert(Long alertId) {
        alertRepo.findById(alertId).ifPresent(alert -> {
            alert.setAcknowledged(true);
            alertRepo.save(alert);
        });
    }
}