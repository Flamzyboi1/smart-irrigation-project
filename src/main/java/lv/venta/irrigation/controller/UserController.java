package lv.venta.irrigation.controller;

import lv.venta.irrigation.model.AppUser;
import lv.venta.irrigation.repo.AppUserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {
    private final AppUserRepository repo;
    public UserController(AppUserRepository repo){this.repo=repo;}

    @GetMapping
    public ResponseEntity<List<UserDto>> all(){
        List<UserDto> result=new ArrayList<>();
        for(AppUser user:repo.findAll()) if(user!=null) result.add(new UserDto(user));
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<UserDto> create(@RequestBody CreateRequest request){
        if(request==null||request.username==null||request.username.isBlank()) return ResponseEntity.badRequest().build();
        if(repo.findByUsername(request.username)!=null) return ResponseEntity.status(409).build();
        AppUser user=new AppUser();
        user.setFullName(request.fullName); user.setUsername(request.username); user.setEmail(request.email); user.setPassword(request.password); user.setRole(request.role==null?"FARMER":request.role); user.setActive(true);
        return ResponseEntity.ok(new UserDto(repo.save(user)));
    }

    @PostMapping("/{id}/toggle")
    public ResponseEntity<UserDto> toggle(@PathVariable Long id,@RequestBody(required=false) ToggleRequest request){
        return repo.findById(id).map(u->{u.setActive(request==null||request.active==null?!u.isActive():request.active);return ResponseEntity.ok(new UserDto(repo.save(u)));}).orElseGet(()->ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){if(!repo.existsById(id))return ResponseEntity.notFound().build();repo.deleteById(id);return ResponseEntity.noContent().build();}

    public static class CreateRequest{public String fullName,username,email,password,role;}
    public static class ToggleRequest{public Boolean active;}
    public static class UserDto{public Long id;public String fullName,username,email,role;public boolean active;public UserDto(AppUser u){id=u.getId();fullName=u.getFullName();username=u.getUsername();email=u.getEmail();role=u.getRole();active=u.isActive();}}
}
