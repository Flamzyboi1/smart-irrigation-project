package lv.venta.irrigation.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class IrrigationZone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String cropType;
    private double centerLat;
    private double centerLng;
    private double radiusMeters;
    private double moistureThreshold;
    private boolean valveOpen;
    private String status;

    public IrrigationZone() {}

    public IrrigationZone(String name, String cropType, double centerLat,
                          double centerLng, double radiusMeters,
                          double moistureThreshold, String status) {
        this.name = name;
        this.cropType = cropType;
        this.centerLat = centerLat;
        this.centerLng = centerLng;
        this.radiusMeters = radiusMeters;
        this.moistureThreshold = moistureThreshold;
        this.valveOpen = false;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCropType() { return cropType; }
    public void setCropType(String cropType) { this.cropType = cropType; }
    public double getCenterLat() { return centerLat; }
    public void setCenterLat(double centerLat) { this.centerLat = centerLat; }
    public double getCenterLng() { return centerLng; }
    public void setCenterLng(double centerLng) { this.centerLng = centerLng; }
    public double getRadiusMeters() { return radiusMeters; }
    public void setRadiusMeters(double radiusMeters) { this.radiusMeters = radiusMeters; }
    public double getMoistureThreshold() { return moistureThreshold; }
    public void setMoistureThreshold(double moistureThreshold) { this.moistureThreshold = moistureThreshold; }
    public boolean isValveOpen() { return valveOpen; }
    public void setValveOpen(boolean valveOpen) { this.valveOpen = valveOpen; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "IrrigationZone{id=" + id + ", name=" + name +
               ", cropType=" + cropType + ", status=" + status +
               ", valveOpen=" + valveOpen + "}";
    }
}