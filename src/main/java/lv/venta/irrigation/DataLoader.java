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
public class DataLoader implements CommandLineRunner
{
    private final ZoneCrudRepository zones;
    private final SensorCrudRepository sensors;
    private final IrrigationService service;
    private final AppUserRepository userRepo;

    public DataLoader(ZoneCrudRepository zones, SensorCrudRepository sensors,
                      IrrigationService service, AppUserRepository userRepo)
    {
        this.zones = zones;
        this.sensors = sensors;
        this.service = service;
        this.userRepo = userRepo;
    }

    @Override
    public void run(String... args)
    {
        // Bootstrap superadmin account if no users exist
        if (userRepo.count() == 0)
        {
            AppUser superadmin = new AppUser();
            superadmin.setFullName("Super Administrator");
            superadmin.setUsername("EecoigmAdmin");
            superadmin.setPassword("Ecoigm123#");
            superadmin.setEmail("admin@irrigation.lv");
            superadmin.setRole("SUPERADMIN");
            superadmin.setActive(true);
            userRepo.save(superadmin);
        }

        // Seed zones and sensors only if DB is empty
        if (zones.count() == 0)
        {
            // Zone 1 - North Field (wheat, near Ventspils)
            IrrigationZone zone1 = new IrrigationZone(
                "North Field", "Wheat",
                57.4085, 21.5680,
                250.0, 30.0, "NEEDS_IRRIGATION"
            );
            zones.save(zone1);

            // Zone 2 - South Field (potatoes)
            IrrigationZone zone2 = new IrrigationZone(
                "South Field", "Potatoes",
                57.3950, 21.5720,
                200.0, 35.0, "IRRIGATING"
            );
            zones.save(zone2);

            // Sensor 1 - assigned to Zone 1
            Sensor s1 = new Sensor(
                "SENSOR-001", "soil_moisture",
                57.4085, 21.5680,
                "North Field - Center", true, zone1
            );
            sensors.save(s1);

            // Sensor 2 - assigned to Zone 2
            Sensor s2 = new Sensor(
                "SENSOR-002", "soil_moisture",
                57.3950, 21.5720,
                "South Field - Center", true, zone2
            );
            sensors.save(s2);
        }
    }
}
