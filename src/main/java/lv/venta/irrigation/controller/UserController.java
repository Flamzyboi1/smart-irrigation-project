package lv.venta.irrigation.controller;

import lv.venta.irrigation.model.AppUser;
import lv.venta.irrigation.repo.AppUserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {
    private final AppUserRepository repo;
    public UserController(AppUserRepository repo){this.repo=repo;}

    @GetMapping
    public List<AppUser> all(){return repo.findAll();}

    @PostMapping
    public ResponseEntity<AppUser> create(@RequestBody AppUser user){
        if(user.getUsername()==null || user.getUsername().isBlank()) return ResponseEntity.badRequest().build();
        if(repo.findByUsername(user.getUsername())!=null) return ResponseEntity.status(409).build();
        user.setActive(true);
        return ResponseEntity.ok(repo.save(user));
    }

    @PostMapping("/{id}/toggle")
    public ResponseEntity<AppUser> toggle(@PathVariable Long id,@RequestBody(required=false) ToggleRequest request){
        return repo.findById(id).map(u->{u.setActive(request==null || request.active==null ? !u.isActive() : request.active);return ResponseEntity.ok(repo.save(u));}).orElseGet(()->ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){if(!repo.existsById(id))return ResponseEntity.notFound().build();repo.deleteById(id);return ResponseEntity.noContent().build();}

    public static class ToggleRequest { public Boolean active; }
}
