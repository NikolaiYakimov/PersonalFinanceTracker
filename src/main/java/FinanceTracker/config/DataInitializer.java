package FinanceTracker.config;

import FinanceTracker.entity.Role;
import FinanceTracker.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {
   @Bean
   public CommandLineRunner initRoles(RoleRepository roleRepository){
       return args -> {

           if(roleRepository.findByName("ROLE_USER").isEmpty()){
               Role userRole=new Role();
               userRole.setName("ROLE_USER");
               roleRepository.save(userRole);
           }

           if(roleRepository.findByName("ROLE_ADMIN").isEmpty()){
               Role adminRole=new Role();
               adminRole.setName("ADMIN_ROLE");
               roleRepository.save(adminRole);
           }

       };
   }

}
