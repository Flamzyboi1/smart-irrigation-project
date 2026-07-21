package lv.venta.irrigation.repo;

import lv.venta.irrigation.model.Sensor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SensorCrudRepository extends CrudRepository<Sensor, Long> {

    // Find sensor by its unique string ID e.g. "Z1-MOIST"
    Sensor findBySensorId(String sensorId);
}