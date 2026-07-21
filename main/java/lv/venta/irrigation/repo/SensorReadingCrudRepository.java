package lv.venta.irrigation.repo;

import lv.venta.irrigation.model.Sensor;
import lv.venta.irrigation.model.SensorReading;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SensorReadingCrudRepository extends CrudRepository<SensorReading, Long> {

    // Get last 10 readings for a sensor ordered by newest first
    List<SensorReading> findTop10BySensorOrderByTimestampDesc(Sensor sensor);

    // Get all readings for a specific sensor
    List<SensorReading> findBySensorOrderByTimestampDesc(Sensor sensor);
}