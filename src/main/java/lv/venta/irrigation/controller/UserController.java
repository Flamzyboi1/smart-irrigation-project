package lv.venta.irrigation.controller;

import lv.venta.irrigation.model.AppUser;
import lv.venta.irrigation.repo.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private AppUserRepository userRepo;

    @GetMapping
    public List<AppUser> getAllUsers() {
        List<AppUser> list = new ArrayList<>();
        userRepo.findAll().forEach(list::add);
        return list;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AppUser user) {
        if (userRepo.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username already exists"));
        }
        if (userRepo.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email already registered"));
        }
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("FARMER");
        }
        user.setActive(true);
        AppUser saved = userRepo.save(user);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        Optional<AppUser> found = userRepo.findByUsername(username);
        if (found.isPresent() && found.get().getPassword().equals(password) && found.get().isActive()) {
            AppUser u = found.get();
            return ResponseEntity.ok(Map.of(
                "id", u.getId(),
                "username", u.getUsername(),
                "fullName", u.getFullName(),
                "role", u.getRole(),
                "email", u.getEmail()
            ));
        }
        return ResponseEntity.status(401).body(Map.of("error", "Invalid username or password"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (!userRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        userRepo.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "User deleted"));
    }

    @PutMapping("/{id}/toggle")
    public ResponseEntity<?> toggleUser(@PathVariable Long id) {
        Optional<AppUser> opt = userRepo.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        AppUser u = opt.get();
        u.setActive(!u.isActive());
        userRepo.save(u);
        return ResponseEntity.ok(Map.of("active", u.isActive()));
    }
}
