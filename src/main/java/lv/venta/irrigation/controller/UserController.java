package lv.venta.irrigation.controller;

import lv.venta.irrigation.model.AppUser;
import lv.venta.irrigation.repo.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private AppUserRepository userRepo;

    // In-memory session store: token -> AppUser
    private static final Map<String, AppUser> sessions = new ConcurrentHashMap<>();

    // Email regex pattern
    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    // ---- LOGIN ----
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        Optional<AppUser> found = userRepo.findByUsername(username);
        if (found.isPresent() && found.get().getPassword().equals(password) && found.get().isActive()) {
            AppUser u = found.get();
            String token = UUID.randomUUID().toString();
            sessions.put(token, u);
            return ResponseEntity.ok(Map.of(
                "token", token,
                "id", u.getId(),
                "username", u.getUsername(),
                "fullName", u.getFullName(),
                "role", u.getRole(),
                "email", u.getEmail()
            ));
        }
        return ResponseEntity.status(401).body(Map.of("error", "Invalid username or password"));
    }

    // ---- LOGOUT ----
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value="X-Auth-Token", required=false) String token) {
        if (token != null) sessions.remove(token);
        return ResponseEntity.ok(Map.of("message", "Logged out"));
    }

    // ---- WHOAMI ----
    @GetMapping("/me")
    public ResponseEntity<?> whoami(@RequestHeader(value="X-Auth-Token", required=false) String token) {
        AppUser u = sessions.get(token);
        if (u == null) return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        return ResponseEntity.ok(Map.of(
            "id", u.getId(),
            "username", u.getUsername(),
            "fullName", u.getFullName(),
            "role", u.getRole(),
            "email", u.getEmail()
        ));
    }

    // ---- GET ALL USERS (ADMIN/SUPERADMIN only) ----
    @GetMapping
    public ResponseEntity<?> getAllUsers(@RequestHeader(value="X-Auth-Token", required=false) String token) {
        AppUser caller = sessions.get(token);
        if (caller == null) return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        if (!caller.getRole().equals("ADMIN") && !caller.getRole().equals("SUPERADMIN")) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }
        List<AppUser> list = new ArrayList<>();
        userRepo.findAll().forEach(list::add);
        return ResponseEntity.ok(list);
    }

    // ---- REGISTER (ADMIN/SUPERADMIN only) ----
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestHeader(value="X-Auth-Token", required=false) String token,
            @RequestBody AppUser user) {
        AppUser caller = sessions.get(token);
        if (caller == null) return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        if (!caller.getRole().equals("ADMIN") && !caller.getRole().equals("SUPERADMIN")) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }
        // Validate email with regex
        if (user.getEmail() == null || !EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid email format"));
        }
        if (userRepo.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username already exists"));
        }
        if (userRepo.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email already registered"));
        }
        // Only SUPERADMIN can create ADMIN accounts
        if ("ADMIN".equals(user.getRole()) && !caller.getRole().equals("SUPERADMIN")) {
            return ResponseEntity.status(403).body(Map.of("error", "Only SUPERADMIN can create ADMIN accounts"));
        }
        if (user.getRole() == null || user.getRole().isEmpty()) user.setRole("FARMER");
        user.setActive(true);
        AppUser saved = userRepo.save(user);
        return ResponseEntity.ok(saved);
    }

    // ---- DELETE USER (ADMIN/SUPERADMIN only) ----
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(
            @RequestHeader(value="X-Auth-Token", required=false) String token,
            @PathVariable Long id) {
        AppUser caller = sessions.get(token);
        if (caller == null) return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        if (!caller.getRole().equals("ADMIN") && !caller.getRole().equals("SUPERADMIN")) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }
        if (!userRepo.existsById(id)) return ResponseEntity.notFound().build();
        userRepo.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "User deleted"));
    }

    // ---- TOGGLE USER (ADMIN/SUPERADMIN only) ----
    @PutMapping("/{id}/toggle")
    public ResponseEntity<?> toggleUser(
            @RequestHeader(value="X-Auth-Token", required=false) String token,
            @PathVariable Long id) {
        AppUser caller = sessions.get(token);
        if (caller == null) return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        if (!caller.getRole().equals("ADMIN") && !caller.getRole().equals("SUPERADMIN")) {
            return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
        }
        Optional<AppUser> opt = userRepo.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        AppUser u = opt.get();
        u.setActive(!u.isActive());
        userRepo.save(u);
        return ResponseEntity.ok(Map.of("active", u.isActive()));
    }
}
