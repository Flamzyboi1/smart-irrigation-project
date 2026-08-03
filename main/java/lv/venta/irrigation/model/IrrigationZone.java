package lv.venta.irrigation.model;
import jakarta.persistence.*;
@Entity public class IrrigationZone {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 private String name; private String cropType; private double centerLat; private double centerLng; private double radiusMeters; private double moistureThreshold; private boolean valveOpen; private String status;
 public IrrigationZone(){} public IrrigationZone(String name,String cropType,double lat,double lng,double radius,double threshold,String status){this.name=name;this.cropType=cropType;centerLat=lat;centerLng=lng;radiusMeters=radius;moistureThreshold=threshold;this.status=status;}
 public Long getId(){return id;} public void setId(Long v){id=v;} public String getName(){return name;} public void setName(String v){name=v;} public String getCropType(){return cropType;} public void setCropType(String v){cropType=v;} public double getCenterLat(){return centerLat;} public void setCenterLat(double v){centerLat=v;} public double getCenterLng(){return centerLng;} public void setCenterLng(double v){centerLng=v;} public double getRadiusMeters(){return radiusMeters;} public void setRadiusMeters(double v){radiusMeters=v;} public double getMoistureThreshold(){return moistureThreshold;} public void setMoistureThreshold(double v){moistureThreshold=v;} public boolean isValveOpen(){return valveOpen;} public void setValveOpen(boolean v){valveOpen=v;} public String getStatus(){return status;} public void setStatus(String v){status=v;}
}
