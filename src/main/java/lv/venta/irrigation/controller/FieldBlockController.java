package lv.venta.irrigation.controller;

import lv.venta.irrigation.model.FieldBlock;
import lv.venta.irrigation.repo.FieldBlockRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/fields")
@CrossOrigin
public class FieldBlockController {
    private final FieldBlockRepository repository;
    public FieldBlockController(FieldBlockRepository repository){this.repository=repository;}
    @GetMapping public List<FieldBlock> all(){return repository.findAll();}
    @GetMapping("/{id}") public ResponseEntity<FieldBlock> one(@PathVariable Long id){return repository.findById(id).map(ResponseEntity::ok).orElseGet(()->ResponseEntity.notFound().build());}
    @PostMapping public ResponseEntity<FieldBlock> create(@RequestBody FieldBlock field){return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(field));}
    @PutMapping("/{id}") public ResponseEntity<FieldBlock> update(@PathVariable Long id,@RequestBody FieldBlock in){return repository.findById(id).map(f->{f.setName(in.getName());f.setCadastralNumber(in.getCadastralNumber());f.setCropType(in.getCropType());f.setAreaHa(in.getAreaHa());f.setGeometryGeoJson(in.getGeometryGeoJson());f.setSource(in.getSource());f.setGrafanaUrl(in.getGrafanaUrl());f.setActive(in.isActive());return ResponseEntity.ok(repository.save(f));}).orElseGet(()->ResponseEntity.notFound().build());}
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id){if(!repository.existsById(id))return ResponseEntity.notFound().build();repository.deleteById(id);return ResponseEntity.noContent().build();}
}
