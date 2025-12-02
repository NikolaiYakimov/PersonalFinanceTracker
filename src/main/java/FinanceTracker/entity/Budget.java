package FinanceTracker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@Table(name="budgets")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    @Column(nullable = false)
    private BigDecimal limitAmount=BigDecimal.valueOf(300);

    @Builder.Default
    @Column(nullable = false)
    private LocalDate startDate=LocalDate.now().withDayOfMonth(1);

    @Builder.Default
    @Column(nullable = false)
    private LocalDate endDate=LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id",nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;
}
