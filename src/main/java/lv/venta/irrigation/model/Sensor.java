package lv.venta.irrigation.model;
import jakarta.persistence.*;
@Entity @Table(uniqueConstraints=@UniqueConstraint(columnNames="sensorId")) public class Sensor {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; private String sensorId; private String type; private double latitude; private double longitude; private String locationLabel; private boolean active;
 @ManyToOne @JoinColumn(name="zone_id") private IrrigationZone zone;
 public Sensor(){} public Sensor(String id,String type,double lat,double lng,String label,boolean active,IrrigationZone zone){sensorId=id;this.type=type;latitude=lat;longitude=lng;locationLabel=label;this.active=active;this.zone=zone;}
 public Long getId(){return id;} public void setId(Long v){id=v;} public String getSensorId(){return sensorId;} public void setSensorId(String v){sensorId=v;} public String getType(){return type;} public void setType(String v){type=v;} public double getLatitude(){return latitude;} public void setLatitude(double v){latitude=v;} public double getLongitude(){return longitude;} public void setLongitude(double v){longitude=v;} public String getLocationLabel(){return locationLabel;} public void setLocationLabel(String v){locationLabel=v;} public boolean isActive(){return active;} public void setActive(boolean v){active=v;} public IrrigationZone getZone(){return zone;} public void setZone(IrrigationZone v){zone=v;}
}
