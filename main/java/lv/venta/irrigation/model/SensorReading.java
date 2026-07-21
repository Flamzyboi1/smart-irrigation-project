package lv.venta.irrigation.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
public class SensorReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sensor_id")
    private Sensor sensor;

    private double soilMoisture;
    private double temperature;
    private double rainfall;
    private double flowRate;
    private LocalDateTime timestamp;
    private String recommendation;

    public SensorReading() {}

    public SensorReading(Sensor sensor, double soilMoisture, double temperature,
                         double rainfall, double flowRate,
                         LocalDateTime timestamp, String recommendation) {
        this.sensor = sensor;
        this.soilMoisture = soilMoisture;
        this.temperature = temperature;
        this.rainfall = rainfall;
        this.flowRate = flowRate;
        this.timestamp = timestamp;
        this.recommendation = recommendation;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Sensor getSensor() { return sensor; }
    public void setSensor(Sensor sensor) { this.sensor = sensor; }
    public double getSoilMoisture() { return soilMoisture; }
    public void setSoilMoisture(double soilMoisture) { this.soilMoisture = soilMoisture; }
    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }
    public double getRainfall() { return rainfall; }
    public void setRainfall(double rainfall) { this.rainfall = rainfall; }
    public double getFlowRate() { return flowRate; }
    public void setFlowRate(double flowRate) { this.flowRate = flowRate; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }

    @Override
    public String toString() {
        return "SensorReading{id=" + id + ", soilMoisture=" + soilMoisture +
               ", temperature=" + temperature + ", recommendation=" + recommendation + "}";
    }
}