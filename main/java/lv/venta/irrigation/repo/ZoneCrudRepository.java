package lv.venta.irrigation.repo;

import lv.venta.irrigation.model.IrrigationZone;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ZoneCrudRepository extends CrudRepository<IrrigationZone, Long> {
}