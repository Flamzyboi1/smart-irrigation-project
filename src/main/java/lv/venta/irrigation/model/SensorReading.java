package lv.venta.irrigation.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "sensor_reading")
public class SensorReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sensor_id")
    private Sensor sensor;

    private double soilPercent;
    private double volumetricWaterContent;
    private double temperature;
    private double humidity;
    private double rainfall;
    private double flowRate;
    private double batteryVoltage;
    private LocalDateTime timestamp;
    private String recommendation;

    public SensorReading() {
    }

    public SensorReading(Sensor sensor,
                         double soilPercent,
                         double volumetricWaterContent,
                         double temperature,
                         double humidity,
                         double rainfall,
                         double flowRate,
                         double batteryVoltage,
                         String recommendation) {
        this.sensor = sensor;
        this.soilPercent = soilPercent;
        this.volumetricWaterContent = volumetricWaterContent;
        this.temperature = temperature;
        this.humidity = humidity;
        this.rainfall = rainfall;
        this.flowRate = flowRate;
        this.batteryVoltage = batteryVoltage;
        this.recommendation = recommendation;
        this.timestamp = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    public double getSoilPercent() {
        return soilPercent;
    }

    public void setSoilPercent(double soilPercent) {
        this.soilPercent = soilPercent;
    }

    public double getVolumetricWaterContent() {
        return volumetricWaterContent;
    }

    public void setVolumetricWaterContent(double volumetricWaterContent) {
        this.volumetricWaterContent = volumetricWaterContent;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public double getRainfall() {
        return rainfall;
    }

    public void setRainfall(double rainfall) {
        this.rainfall = rainfall;
    }

    public double getFlowRate() {
        return flowRate;
    }

    public void setFlowRate(double flowRate) {
        this.flowRate = flowRate;
    }

    public double getBatteryVoltage() {
        return batteryVoltage;
    }

    public void setBatteryVoltage(double batteryVoltage) {
        this.batteryVoltage = batteryVoltage;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    @Override
    public String toString() {
        return "SensorReading{id=" + id
                + ", soilPercent=" + soilPercent
                + ", volumetricWaterContent=" + volumetricWaterContent
                + ", temperature=" + temperature
                + ", humidity=" + humidity
                + ", rainfall=" + rainfall
                + ", flowRate=" + flowRate
                + ", batteryVoltage=" + batteryVoltage
                + ", recommendation='" + recommendation + '\''
                + ", timestamp=" + timestamp
                + '}';
    }
}