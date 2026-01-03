package FinanceTracker.repository;

import FinanceTracker.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    List<Budget> findByUserId(Long userId);

    Optional<Budget> findByUserIdAndCategoryId(Long userId, Long categoryId);

    @Query("SELECT b FROM Budget b " +
            "WHERE b.user.id = :userId " +
            "AND b.category.id = :categoryId " +
            "AND :date BETWEEN b.startDate AND b.endDate")
    Optional<Budget> findActiveBudgetByCategory(@Param("userId") Long userId,
                                                @Param("categoryId") Long categoryId,
                                                @Param("date") LocalDate date);

    @Query("SELECT b FROM Budget b " +
            "WHERE b.user.id = :userId " +
            "AND :date BETWEEN b.startDate AND b.endDate")
    List<Budget> findAllActiveBudgets(@Param("userId") Long userId,
                                      @Param("date") LocalDate date);

    boolean existsByUserIdAndCategoryIdAndStartDate(Long userId,Long categoryId,LocalDate startDate);
}
