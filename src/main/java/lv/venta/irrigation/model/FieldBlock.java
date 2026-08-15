package lv.venta.irrigation.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class FieldBlock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String cadastralNumber;
    private String cropType;
    private Double areaHa;
    private String geometryGeoJson;
    private String source;
    private String grafanaUrl;
    private boolean active = true;

    public FieldBlock() {}
    public Long getId(){return id;} public void setId(Long id){this.id=id;}
    public String getName(){return name;} public void setName(String v){name=v;}
    public String getCadastralNumber(){return cadastralNumber;} public void setCadastralNumber(String v){cadastralNumber=v;}
    public String getCropType(){return cropType;} public void setCropType(String v){cropType=v;}
    public Double getAreaHa(){return areaHa;} public void setAreaHa(Double v){areaHa=v;}
    public String getGeometryGeoJson(){return geometryGeoJson;} public void setGeometryGeoJson(String v){geometryGeoJson=v;}
    public String getSource(){return source;} public void setSource(String v){source=v;}
    public String getGrafanaUrl(){return grafanaUrl;} public void setGrafanaUrl(String v){grafanaUrl=v;}
    public boolean isActive(){return active;} public void setActive(boolean v){active=v;}
}
