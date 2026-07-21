package lv.venta.irrigation.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Sensor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String sensorId;
    private String type;
    private double latitude;
    private double longitude;
    private String locationLabel;
    private boolean active;

    @ManyToOne
    @JoinColumn(name = "zone_id")
    private IrrigationZone zone;

    public Sensor() {}

    public Sensor(String sensorId, String type, double latitude, double longitude,
                  String locationLabel, boolean active, IrrigationZone zone) {
        this.sensorId = sensorId;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationLabel = locationLabel;
        this.active = active;
        this.zone = zone;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSensorId() { return sensorId; }
    public void setSensorId(String sensorId) { this.sensorId = sensorId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public String getLocationLabel() { return locationLabel; }
    public void setLocationLabel(String locationLabel) { this.locationLabel = locationLabel; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public IrrigationZone getZone() { return zone; }
    public void setZone(IrrigationZone zone) { this.zone = zone; }

    @Override
    public String toString() {
        return "Sensor{id=" + id + ", sensorId=" + sensorId +
               ", type=" + type + ", active=" + active + "}";
    }
}