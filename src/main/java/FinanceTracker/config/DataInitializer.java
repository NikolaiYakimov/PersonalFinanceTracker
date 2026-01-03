package FinanceTracker.config;

import FinanceTracker.entity.RoleEntity;
import FinanceTracker.enums.Role;
import FinanceTracker.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class DataInitializer {
   @Bean
   public CommandLineRunner initRoles(RoleRepository roleRepository){
       return args -> {

           if(roleRepository.findByName(Role.ROLE_USER).isEmpty()){
               RoleEntity userRole=new RoleEntity();
               userRole.setName(Role.ROLE_USER);
               roleRepository.save(userRole);
           }

           if(roleRepository.findByName(Role.ROLE_ADMIN).isEmpty()){
               RoleEntity adminRole=new RoleEntity();
               adminRole.setName(Role.ROLE_ADMIN);
               roleRepository.save(adminRole);
           }

       };
   }

}
