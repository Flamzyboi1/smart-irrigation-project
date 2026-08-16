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
    public ResponseEntity<?> login(@RequestBody LoginRequest request){
        if(request==null||request.username==null||request.password==null) return ResponseEntity.badRequest().body(new ErrorResponse("Username and password are required"));
        AppUser user=repo.findByUsername(request.username);
        if(user==null||!user.isActive()||!request.password.equals(user.getPassword())) return ResponseEntity.status(401).body(new ErrorResponse("Invalid username or password"));
        return ResponseEntity.ok(new LoginResponse(user));
    }
    public static class LoginRequest{public String username,password;}
    public static class ErrorResponse{public String message;public ErrorResponse(String m){message=m;}}
    public static class LoginResponse{public String username,fullName,email,role;public boolean active;public LoginResponse(AppUser u){username=u.getUsername();fullName=u.getFullName();email=u.getEmail();role=u.getRole();active=u.isActive();}}
}
