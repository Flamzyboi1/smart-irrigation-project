package lv.venta.irrigation.repo;

import lv.venta.irrigation.model.Plant;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlantCrudRepository extends CrudRepository<Plant, Long> {

    // Find plant profile by name for zone lookup
    Plant findByName(String name);
}