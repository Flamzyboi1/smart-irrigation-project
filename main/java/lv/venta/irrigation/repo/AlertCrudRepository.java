package lv.venta.irrigation.repo;

import lv.venta.irrigation.model.IrrigationAlert;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlertCrudRepository extends CrudRepository<IrrigationAlert, Long> {

    // Get only alerts not yet acknowledged by operator
    Iterable<IrrigationAlert> findByAcknowledgedFalse();
}