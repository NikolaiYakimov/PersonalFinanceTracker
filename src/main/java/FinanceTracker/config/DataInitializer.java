package FinanceTracker.config;

import FinanceTracker.entity.Category;
import FinanceTracker.entity.RoleEntity;
import FinanceTracker.enums.Role;
import FinanceTracker.enums.TransactionType;
import FinanceTracker.repository.CategoryRepository;
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
           }else {
               System.out.println("Role USER already exists.");
           }

           if(roleRepository.findByName(Role.ROLE_ADMIN).isEmpty()){
               RoleEntity adminRole=new RoleEntity();
               adminRole.setName(Role.ROLE_ADMIN);
               roleRepository.save(adminRole);
           }else {
               System.out.println("Role ADMIN already exists.");
           }

       };
   }

   @Bean
   public CommandLineRunner initCategory(CategoryRepository categoryRepository){
       return args -> {
           createSystemCategoryIfMissing("Заплата",TransactionType.INCOME,categoryRepository);
           createSystemCategoryIfMissing("Бонуси",TransactionType.INCOME,categoryRepository);
           createSystemCategoryIfMissing("Инвестиции",TransactionType.INCOME,categoryRepository);

           createSystemCategoryIfMissing("Храна",TransactionType.EXPENSE,categoryRepository);
           createSystemCategoryIfMissing("Спорт",TransactionType.EXPENSE,categoryRepository);
           createSystemCategoryIfMissing("Транспорт",TransactionType.EXPENSE,categoryRepository);
           createSystemCategoryIfMissing("Наем",TransactionType.EXPENSE,categoryRepository);
           createSystemCategoryIfMissing("Здраве",TransactionType.EXPENSE,categoryRepository);
           createSystemCategoryIfMissing("Шопинг",TransactionType.EXPENSE,categoryRepository);


       };
   }


   private void  createSystemCategoryIfMissing(String name, TransactionType type, CategoryRepository repository){
       if(!repository.existsByNameAndUserIsNull(name)){
           Category category=new Category();
           category.setName(name);
           category.setType(type);
           category.setUser(null);

           repository.save(category);
           System.out.println("System Category has been created.");
       }
   }

}
