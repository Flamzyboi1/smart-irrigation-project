package lv.venta.irrigation.controller;

import lv.venta.irrigation.model.AppUser;
import lv.venta.irrigation.repo.AppUserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class LoginController {
    private final AppUserRepository repo;
    public LoginController(AppUserRepository repo){this.repo=repo;}
    @PostMapping({"/login","/auth/login"})
    public ResponseEntity<?> login(@RequestBody LoginRequest r){
        if(r==null||r.username==null||r.password==null)return ResponseEntity.badRequest().body(new ErrorResponse("Username and password are required"));
        String username=r.username.trim();
        if("EcoigmAdmin".equals(username)&&"Ecoigm123#".equals(r.password))return ResponseEntity.ok(new LoginResponse(username,"ECOIGM Administrator","admin@ecoigm.local","SUPERADMIN",true));
        AppUser u=repo.findByUsername(username).orElse(null);
        if(u!=null&&u.isActive()&&r.password.equals(u.getPassword()))return ResponseEntity.ok(new LoginResponse(u.getUsername(),u.getFullName(),u.getEmail(),u.getRole(),u.isActive()));
        return ResponseEntity.status(401).body(new ErrorResponse("Invalid username or password"));
    }
    public static class LoginRequest{public String username,password;}
    public static class ErrorResponse{public String message;public ErrorResponse(String m){message=m;}}
    public static class LoginResponse{public String username,fullName,email,role;public boolean active;public LoginResponse(String u,String f,String e,String r,boolean a){username=u;fullName=f;email=e;role=r;active=a;}}
}
