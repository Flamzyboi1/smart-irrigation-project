package lv.venta.irrigation.service;
import java.time.LocalDateTime;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lv.venta.irrigation.model.*;
import lv.venta.irrigation.repo.*;
@Service
public class IrrigationService {
 @Autowired private SensorCrudRepository sensorRepo;
 @Autowired private SensorReadingCrudRepository readingRepo;
 @Autowired private ZoneCrudRepository zoneRepo;
 @Autowired private AlertCrudRepository alertRepo;

 public SensorReading saveReading(String sensorId,double soilPercent,double temperature,double humidity,double rainfall,double flowRate,double batteryVoltage){
 Sensor sensor=sensorRepo.findBySensorId(sensorId);
 if(sensor==null)throw new RuntimeException("Sensor not found: "+sensorId);
 IrrigationZone zone=sensor.getZone();
 double threshold=resolveCropThreshold(zone);
 double maxTemp=zone!=null&&zone.getMaxTemperature()>0?zone.getMaxTemperature():35.0;
 double vwc=calculateVwc(soilPercent,zone);
 SensorReading r=new SensorReading();
 r.setSensor(sensor);r.setSoilPercent(soilPercent);r.setVolumetricWaterContent(vwc);
 r.setTemperature(temperature);r.setHumidity(humidity);r.setRainfall(rainfall);
 r.setFlowRate(flowRate);r.setBatteryVoltage(batteryVoltage);r.setTimestamp(LocalDateTime.now());
 if(vwc<threshold-10){r.setRecommendation("IRRIGATE_NOW");if(zone!=null){zone.setStatus("ALERT");zone.setValveOpen(true);}createAlert(sensorId,zone,"LOW_MOISTURE","CRITICAL","CRITICAL: VWC "+String.format("%.1f",vwc)+"% far below threshold "+threshold+"%");}
 else if(vwc<threshold){r.setRecommendation("IRRIGATE");if(zone!=null){zone.setStatus("DRY");zone.setValveOpen(false);}createAlert(sensorId,zone,"LOW_MOISTURE","WARNING","WARNING: VWC "+String.format("%.1f",vwc)+"% below threshold "+threshold+"%");}
 else if(vwc>80){r.setRecommendation("EXCESS_WATER");if(zone!=null){zone.setStatus("WET");zone.setValveOpen(false);}createAlert(sensorId,zone,"HIGH_MOISTURE","INFO","INFO: Excess moisture "+String.format("%.1f",vwc)+"%");}
 else{r.setRecommendation("ADEQUATE");if(zone!=null){zone.setStatus("NORMAL");zone.setValveOpen(false);}}
 if(temperature>maxTemp)createAlert(sensorId,zone,"HIGH_TEMP","WARNING","WARNING: Temperature "+String.format("%.1f",temperature)+"C exceeds limit "+maxTemp+"C");
 if(zone!=null)zoneRepo.save(zone);
 return readingRepo.save(r);
 }

 public IrrigationZone updateCropType(Long zoneId,String cropType){
 IrrigationZone zone=zoneRepo.findById(zoneId).orElseThrow(()->new RuntimeException("Zone not found: "+zoneId));
 String c=cropType==null?"":cropType.trim();
 if(c.isEmpty())throw new RuntimeException("Crop type cannot be empty");
 zone.setCropType(c);
 zone.setMoistureThreshold(getThresholdForCrop(c));
 return zoneRepo.save(zone);
 }

 public IrrigationZone updateZoneSettings(Long zoneId,String cropType,double moistureThreshold,double maxTemperature){
 IrrigationZone zone=zoneRepo.findById(zoneId).orElseThrow(()->new RuntimeException("Zone not found: "+zoneId));
 if(cropType!=null&&!cropType.trim().isEmpty())zone.setCropType(cropType.trim());
 if(moistureThreshold>0)zone.setMoistureThreshold(moistureThreshold);
 if(maxTemperature>0)zone.setMaxTemperature(maxTemperature);
 return zoneRepo.save(zone);
 }

 private double resolveCropThreshold(IrrigationZone zone){
 if(zone==null)return 25.0;
 if(zone.getMoistureThreshold()>0)return zone.getMoistureThreshold();
 String crop=zone.getCropType();
 if(crop==null||crop.isBlank())return 25.0;
 return getThresholdForCrop(crop);
 }

 private double getThresholdForCrop(String crop){
 switch(crop.trim().toLowerCase()){
 case"tomato":case"tomatoes":return 30.0;
 case"potato":case"potatoes":return 22.0;
 case"wheat":return 20.0;
 case"lettuce":return 35.0;
 case"corn":case"maize":return 25.0;
 case"rice":return 40.0;
 case"soybean":return 28.0;
 default:return 25.0;
 }
 }

 private double calculateVwc(double soilPercent,IrrigationZone zone){
 if(zone==null||zone.getCropType()==null)return soilPercent;
 switch(zone.getCropType().trim().toLowerCase()){
 case"tomato":case"tomatoes":return soilPercent*0.95;
 case"potato":case"potatoes":return soilPercent*0.75;
 case"lettuce":return soilPercent*1.05;
 case"wheat":return soilPercent*0.70;
 default:return soilPercent*0.85;
 }
 }

 private void createAlert(String sensorId,IrrigationZone zone,String type,String severity,String message){
 IrrigationAlert a=new IrrigationAlert();
 a.setSensorId(sensorId);a.setZoneName(zone!=null?zone.getName():"Unknown");
 a.setAlertType(type);a.setSeverity(severity);a.setMessage(message);
 a.setAcknowledged(false);a.setCreatedAt(LocalDateTime.now());
 alertRepo.save(a);
 }

 public List<Map<String,Object>> getAllSensorsWithLatestReading(){
 List<Map<String,Object>> result=new ArrayList<>();
 for(Sensor sensor:sensorRepo.findAll()){
 Map<String,Object> map=new LinkedHashMap<>();
 map.put("id",sensor.getId());map.put("sensorId",sensor.getSensorId());
 map.put("type",sensor.getType());map.put("latitude",sensor.getLatitude());
 map.put("longitude",sensor.getLongitude());map.put("label",sensor.getLocationLabel());
 map.put("active",sensor.isActive());
 IrrigationZone zone=sensor.getZone();
 if(zone!=null){
 map.put("zoneName",zone.getName());map.put("zoneId",zone.getId());
 map.put("zoneStatus",zone.getStatus());map.put("valveOpen",zone.isValveOpen());
 map.put("threshold",resolveCropThreshold(zone));map.put("cropType",zone.getCropType());
 map.put("moistureThreshold",zone.getMoistureThreshold());
 map.put("maxTemperature",zone.getMaxTemperature()>0?zone.getMaxTemperature():35.0);
 }
 List<SensorReading> readings=readingRepo.findTop10BySensorOrderByTimestampDesc(sensor);
 if(!readings.isEmpty()){
 SensorReading latest=readings.get(0);
 map.put("soilPercent",latest.getSoilPercent());map.put("volumetricWaterContent",latest.getVolumetricWaterContent());
 map.put("temperature",latest.getTemperature());map.put("humidity",latest.getHumidity());
 map.put("rainfall",latest.getRainfall());map.put("flowRate",latest.getFlowRate());
 map.put("batteryVoltage",latest.getBatteryVoltage());map.put("recommendation",latest.getRecommendation());
 map.put("timestamp",latest.getTimestamp()!=null?latest.getTimestamp().toString():"");
 List<Double> history=new ArrayList<>();
 for(SensorReading rd:readings)history.add(rd.getSoilPercent());
 Collections.reverse(history);map.put("history",history);
 }
 result.add(map);
 }
 return result;
 }

 public List<Map<String,Object>> getAllReadings(){
 List<Map<String,Object>> result=new ArrayList<>();
 for(SensorReading r:readingRepo.findAll()){
 Map<String,Object> map=new LinkedHashMap<>();
 map.put("id",r.getId());map.put("sensorId",r.getSensor()!=null?r.getSensor().getSensorId():"N/A");
 map.put("zoneName",r.getSensor()!=null&&r.getSensor().getZone()!=null?r.getSensor().getZone().getName():"N/A");
 map.put("soilPercent",r.getSoilPercent());map.put("volumetricWaterContent",r.getVolumetricWaterContent());
 map.put("temperature",r.getTemperature());map.put("humidity",r.getHumidity());
 map.put("rainfall",r.getRainfall());map.put("flowRate",r.getFlowRate());
 map.put("batteryVoltage",r.getBatteryVoltage());map.put("recommendation",r.getRecommendation());
 map.put("timestamp",r.getTimestamp()!=null?r.getTimestamp().toString():"");
 result.add(map);
 }
 return result;
 }

 public Iterable<IrrigationZone> getAllZones(){return zoneRepo.findAll();}
 public Iterable<IrrigationAlert> getActiveAlerts(){return alertRepo.findByAcknowledgedFalse();}
 public Iterable<IrrigationAlert> getAllAlerts(){return alertRepo.findAll();}
 public void toggleValve(Long id){zoneRepo.findById(id).ifPresent(z->{z.setValveOpen(!z.isValveOpen());zoneRepo.save(z);});}
 public void acknowledgeAlert(Long id){alertRepo.findById(id).ifPresent(a->{a.setAcknowledged(true);alertRepo.save(a);});}
}
