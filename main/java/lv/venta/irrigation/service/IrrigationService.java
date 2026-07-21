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

    public SensorReading saveReading(String sensorId, double moisture,
                                     double temp, double rainfall, double flow) {

        Sensor sensor = sensorRepo.findBySensorId(sensorId);
        if (sensor == null) return null;

        IrrigationZone zone = sensor.getZone();
        double threshold = (zone != null) ? zone.getMoistureThreshold() : 25.0;

        SensorReading reading = new SensorReading();
        reading.setSensor(sensor);
        reading.setSoilMoisture(moisture);
        reading.setTemperature(temp);
        reading.setRainfall(rainfall);
        reading.setFlowRate(flow);
        reading.setTimestamp(LocalDateTime.now());

        if (moisture < threshold - 10) {
            reading.setRecommendation("IRRIGATE_NOW");
            createAlert(sensorId, zone, "LOW_MOISTURE", "CRITICAL",
                "CRITICAL: Moisture at " + String.format("%.1f", moisture)
                + "% far below threshold " + threshold + "%");
            if (zone != null) {
                zone.setStatus("ALERT");
                zone.setValveOpen(true);
            }
        } else if (moisture < threshold) {
            reading.setRecommendation("IRRIGATE");
            createAlert(sensorId, zone, "LOW_MOISTURE", "WARNING",
                "WARNING: Moisture at " + String.format("%.1f", moisture)
                + "% below threshold " + threshold + "%");
            if (zone != null) {
                zone.setStatus("DRY");
            }
        } else if (moisture > 80) {
            reading.setRecommendation("EXCESS_WATER");
            createAlert(sensorId, zone, "HIGH_MOISTURE", "INFO",
                "INFO: Excess moisture at " + String.format("%.1f", moisture) + "%");
            if (zone != null) {
                zone.setStatus("WET");
                zone.setValveOpen(false);
            }
        } else {
            reading.setRecommendation("ADEQUATE");
            if (zone != null) {
                zone.setStatus("NORMAL");
                zone.setValveOpen(false);
            }
        }

        if (temp > 35.0) {
            createAlert(sensorId, zone, "HIGH_TEMP", "WARNING",
                "WARNING: Temperature " + String.format("%.1f", temp)
                + "C exceeds safe limit");
        }

        if (zone != null) {
            zoneRepo.save(zone);
        }

        return readingRepo.save(reading);
    }

    private void createAlert(String sensorId, IrrigationZone zone,
                              String type, String severity, String message) {
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
                map.put("threshold", zone.getMoistureThreshold());
                map.put("cropType", zone.getCropType());
            }

            List<SensorReading> readings = readingRepo
                .findTop10BySensorOrderByTimestampDesc(sensor);

            if (!readings.isEmpty()) {
                SensorReading latest = readings.get(0);
                map.put("moisture", latest.getSoilMoisture());
                map.put("temperature", latest.getTemperature());
                map.put("rainfall", latest.getRainfall());
                map.put("flowRate", latest.getFlowRate());
                map.put("recommendation", latest.getRecommendation());
                map.put("timestamp", latest.getTimestamp().toString());

                List<Double> history = new ArrayList<>();
                for (SensorReading r : readings) {
                    history.add(r.getSoilMoisture());
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
            map.put("sensorId", r.getSensor() != null
                ? r.getSensor().getSensorId() : "N/A");
            map.put("zoneName", r.getSensor() != null
                && r.getSensor().getZone() != null
                ? r.getSensor().getZone().getName() : "N/A");
            map.put("moisture", r.getSoilMoisture());
            map.put("temperature", r.getTemperature());
            map.put("rainfall", r.getRainfall());
            map.put("flowRate", r.getFlowRate());
            map.put("recommendation", r.getRecommendation());
            map.put("timestamp", r.getTimestamp() != null
                ? r.getTimestamp().toString() : "");
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