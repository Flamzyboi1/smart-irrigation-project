package lv.venta.irrigation.controller;

import lv.venta.irrigation.model.FieldBlock;
import lv.venta.irrigation.repo.FieldBlockRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/field-blocks")
@CrossOrigin
public class FieldBlockController {
    private final FieldBlockRepository repo;
    public FieldBlockController(FieldBlockRepository repo){this.repo=repo;}
    @GetMapping public List<FieldBlock> all(){return repo.findAll();}
    @PostMapping public ResponseEntity<FieldBlock> create(@RequestBody CreateRequest r){
        if(r==null||r.name==null||r.name.isBlank()) return ResponseEntity.badRequest().build();
        FieldBlock f=new FieldBlock(); f.setName(r.name); f.setGeometry(r.geometry); f.setCrop(r.crop); f.setAreaHa(r.areaHa); f.setStatus(r.status==null?"Active":r.status);
        return ResponseEntity.ok(repo.save(f));
    }
    public static class CreateRequest{public String name,geometry,crop,status;public Double areaHa;}
}
