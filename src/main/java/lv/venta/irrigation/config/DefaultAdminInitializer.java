package lv.venta.irrigation.config;

import lv.venta.irrigation.model.AppUser;
import lv.venta.irrigation.repo.AppUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DefaultAdminInitializer {
    @Bean
    CommandLineRunner createDefaultAdmin(AppUserRepository repo){
        return args -> {
            AppUser user=repo.findByUsername("EcoigmAdmin").orElse(null);
            if(user==null){
                user=new AppUser(); user.setUsername("EcoigmAdmin"); user.setPassword("Ecoigm123#"); user.setFullName("ECOIGM Administrator"); user.setEmail("admin@ecoigm.local"); user.setRole("SUPERADMIN");
            }
            user.setActive(true); repo.save(user);
        };
    }
}
