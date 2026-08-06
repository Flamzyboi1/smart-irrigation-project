package lv.venta.irrigation;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import lv.venta.irrigation.model.AppUser;
import lv.venta.irrigation.model.IrrigationZone;
import lv.venta.irrigation.model.Sensor;
import lv.venta.irrigation.repo.AppUserRepository;
import lv.venta.irrigation.repo.SensorCrudRepository;
import lv.venta.irrigation.repo.ZoneCrudRepository;
import lv.venta.irrigation.service.IrrigationService;

@Component
public class DataLoader implements CommandLineRunner {

    private final ZoneCrudRepository zones;
    private final SensorCrudRepository sensors;
    private final IrrigationService service;
    private final AppUserRepository userRepo;

    public DataLoader(ZoneCrudRepository zones, SensorCrudRepository sensors,
                      IrrigationService service, AppUserRepository userRepo) {
        this.zones = zones;
        this.sensors = sensors;
        this.service = service;
        this.userRepo = userRepo;
    }

    @Override
    public void run(String... args) {
        // Bootstrap superadmin account if no users exist
        if (userRepo.count() == 0) {
            AppUser superadmin = new AppUser();
            superadmin.setFullName("Super Administrator");
            superadmin.setUsername("superadmin");
            superadmin.setPassword("SuperAdmin2024!");
            superadmin.setEmail("superadmin@ecoigm.lv");
            superadmin.setRole("SUPERADMIN");
            superadmin.setActive(true);
            userRepo.save(superadmin);
        }

        // Bootstrap sensor data if not already loaded
        if (sensors.count() > 0) return;

        IrrigationZone a = zones.save(
                new IrrigationZone(
                        "Tallinn Greenhouse",
                        "Tomato",
                        59.437,
                        24.754,
                        50,
                        30,
                        "NORMAL"
                )
        );
        IrrigationZone b = zones.save(
                new IrrigationZone(
                        "Valmiera Field",
                        "Wheat",
                        57.541,
                        25.427,
                        100,
                        25,
                        "NORMAL"
                )
        );
        sensors.save(new Sensor(
                "SENSOR-001",
                "DHT11 + Capacitive Soil",
                59.437,
                24.754,
                "Tallinn Greenhouse ESP32",
                true,
                a
        ));
        sensors.save(new Sensor(
                "SENSOR-002",
                "DHT22 + Capacitive Soil",
                57.541,
                25.427,
                "Valmiera Field ESP32",
                true,
                b
        ));
        service.saveReading("SENSOR-001", 42.5, 25.3, 60.0, 0.0, 0.0, 3.95);
        service.saveReading("SENSOR-002", 18.0, 21.0, 72.0, 0.0, 0.0, 3.88);
    }
}
