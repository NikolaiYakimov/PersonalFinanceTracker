package FinanceTracker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "currencies")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Currency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true,length = 3)
    private String code;

    @Column(nullable = false,unique = true,length = 5)
    private String symbol;

    @Column(nullable = false,precision = 19,scale = 4)
    private BigDecimal exchangeRate;
}
