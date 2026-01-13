package FinanceTracker.entity;

import FinanceTracker.enums.Role;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleEntity {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false,unique = true)
    private Role name;

//    public Role getName() {
//        return this.name;
//    }
}
