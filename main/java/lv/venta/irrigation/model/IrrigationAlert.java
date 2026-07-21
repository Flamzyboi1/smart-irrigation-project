package lv.venta.irrigation.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class IrrigationAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String sensorId;
    private String zoneName;
    private String alertType;
    private String message;
    private String severity;
    private boolean acknowledged;
    private LocalDateTime createdAt;

    public IrrigationAlert() {}

    public IrrigationAlert(String sensorId, String zoneName, String alertType,
                           String message, String severity, LocalDateTime createdAt) {
        this.sensorId = sensorId;
        this.zoneName = zoneName;
        this.alertType = alertType;
        this.message = message;
        this.severity = severity;
        this.acknowledged = false;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSensorId() { return sensorId; }
    public void setSensorId(String sensorId) { this.sensorId = sensorId; }
    public String getZoneName() { return zoneName; }
    public void setZoneName(String zoneName) { this.zoneName = zoneName; }
    public String getAlertType() { return alertType; }
    public void setAlertType(String alertType) { this.alertType = alertType; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    public boolean isAcknowledged() { return acknowledged; }
    public void setAcknowledged(boolean acknowledged) { this.acknowledged = acknowledged; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "IrrigationAlert{id=" + id + ", sensorId=" + sensorId +
               ", alertType=" + alertType + ", severity=" + severity +
               ", acknowledged=" + acknowledged + "}";
    }
}