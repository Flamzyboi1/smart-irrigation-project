package lv.venta.irrigation.service;

import lv.venta.irrigation.model.IrrigationZone;
import lv.venta.irrigation.model.Sensor;
import lv.venta.irrigation.repo.SensorCrudRepository;
import lv.venta.irrigation.repo.ZoneCrudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Service
public class DataSimulatorService implements CommandLineRunner {

    @Autowired
    private ZoneCrudRepository zoneRepo;

    @Autowired
    private SensorCrudRepository sensorRepo;

    @Autowired
    private IrrigationService irrigationService;

    @Override
    public void run(String... args) throws Exception {

        IrrigationZone z1 = new IrrigationZone(
            "North Field", "Wheat",
            57.3920, 21.5650, 120, 30.0, "NORMAL"
        );
        IrrigationZone z2 = new IrrigationZone(
            "South Greenhouse", "Tomato",
            57.3860, 21.5580, 60, 40.0, "NORMAL"
        );
        IrrigationZone z3 = new IrrigationZone(
            "East Orchard", "Apple",
            57.3950, 21.5720, 200, 25.0, "NORMAL"
        );
        IrrigationZone z4 = new IrrigationZone(
            "West Vineyard", "Grape",
            57.3880, 21.5540, 180, 20.0, "NORMAL"
        );

        zoneRepo.save(z1);
        zoneRepo.save(z2);
        zoneRepo.save(z3);
        zoneRepo.save(z4);

        Sensor s1 = new Sensor(
            "Z1-MOIST", "SOIL_MOISTURE",
            57.3922, 21.5652,
            "North Field Sensor", true, z1
        );
        Sensor s2 = new Sensor(
            "Z2-MOIST", "SOIL_MOISTURE",
            57.3862, 21.5582,
            "Greenhouse Sensor", true, z2
        );
        Sensor s3 = new Sensor(
            "Z3-MOIST", "SOIL_MOISTURE",
            57.3952, 21.5722,
            "Orchard Sensor", true, z3
        );
        Sensor s4 = new Sensor(
            "Z4-MOIST", "SOIL_MOISTURE",
            57.3882, 21.5542,
            "Vineyard Sensor", true, z4
        );

        sensorRepo.save(s1);
        sensorRepo.save(s2);
        sensorRepo.save(s3);
        sensorRepo.save(s4);

        irrigationService.saveReading("Z1-MOIST", 18.5, 28.0, 0.0, 0.8);
        irrigationService.saveReading("Z2-MOIST", 33.0, 31.0, 0.5, 1.2);
        irrigationService.saveReading("Z3-MOIST", 52.0, 22.0, 2.0, 0.5);
        irrigationService.saveReading("Z4-MOIST", 83.0, 19.0, 8.0, 0.3);
    }
}