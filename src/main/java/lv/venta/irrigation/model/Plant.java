package lv.venta.irrigation.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "plant")
public class Plant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Plant name e.g. Wheat, Tomato, Apple, Grape
    private String name;

    // Growing stage name: Germination, Vegetative, Flowering, Fruiting, Harvest
    private String currentStage;

    // Days from planting when this stage starts
    private int stageStartDay;

    // Days from planting when this stage ends
    private int stageEndDay;

    // Base daily water need in litres per m2 per day (FAO-56 standard)
    private double baseDailyWaterLitresPerSqM;

    // Water multiplier for current stage
    // Germination=0.5, Vegetative=0.8, Flowering=1.5, Fruiting=1.3, Harvest=0.6
    private double stageWaterMultiplier;

    // Minimum soil moisture % before irrigation must trigger
    private double minSoilMoisturePercent;

    // Ideal target soil moisture %
    private double optimalSoilMoisturePercent;

    // Maximum safe temperature in Celsius before heat stress alert
    private double maxSafeTemperatureCelsius;

    public Plant() {}

    public Plant(String name, String currentStage, int stageStartDay, int stageEndDay,
                 double baseDailyWaterLitresPerSqM, double stageWaterMultiplier,
                 double minSoilMoisturePercent, double optimalSoilMoisturePercent,
                 double maxSafeTemperatureCelsius) {
        this.name = name;
        this.currentStage = currentStage;
        this.stageStartDay = stageStartDay;
        this.stageEndDay = stageEndDay;
        this.baseDailyWaterLitresPerSqM = baseDailyWaterLitresPerSqM;
        this.stageWaterMultiplier = stageWaterMultiplier;
        this.minSoilMoisturePercent = minSoilMoisturePercent;
        this.optimalSoilMoisturePercent = optimalSoilMoisturePercent;
        this.maxSafeTemperatureCelsius = maxSafeTemperatureCelsius;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCurrentStage() { return currentStage; }
    public void setCurrentStage(String currentStage) { this.currentStage = currentStage; }
    public int getStageStartDay() { return stageStartDay; }
    public void setStageStartDay(int stageStartDay) { this.stageStartDay = stageStartDay; }
    public int getStageEndDay() { return stageEndDay; }
    public void setStageEndDay(int stageEndDay) { this.stageEndDay = stageEndDay; }
    public double getBaseDailyWaterLitresPerSqM() { return baseDailyWaterLitresPerSqM; }
    public void setBaseDailyWaterLitresPerSqM(double v) { this.baseDailyWaterLitresPerSqM = v; }
    public double getStageWaterMultiplier() { return stageWaterMultiplier; }
    public void setStageWaterMultiplier(double stageWaterMultiplier) { this.stageWaterMultiplier = stageWaterMultiplier; }
    public double getMinSoilMoisturePercent() { return minSoilMoisturePercent; }
    public void setMinSoilMoisturePercent(double v) { this.minSoilMoisturePercent = v; }
    public double getOptimalSoilMoisturePercent() { return optimalSoilMoisturePercent; }
    public void setOptimalSoilMoisturePercent(double v) { this.optimalSoilMoisturePercent = v; }
    public double getMaxSafeTemperatureCelsius() { return maxSafeTemperatureCelsius; }
    public void setMaxSafeTemperatureCelsius(double v) { this.maxSafeTemperatureCelsius = v; }

    @Override
    public String toString() {
        return "Plant{id=" + id + ", name=" + name + ", stage=" + currentStage +
               ", baseWater=" + baseDailyWaterLitresPerSqM +
               ", multiplier=" + stageWaterMultiplier + "}";
    }
}