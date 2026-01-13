package FinanceTracker.repository;

import FinanceTracker.entity.Budget;
import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    @Query("SELECT b FROM Budget b " +
            "WHERE b.user.id = :userId " +
            "AND b.endDate < :now ORDER BY b.endDate DESC")
    List<Budget> findAllPastBudgets(@Param("userId") Long userId,
                                    @Param("date") LocalDate date);


    @Query("SELECT COUNT(b) > 0 FROM Budget b " +
            "WHERE b.user.id = :userId " +
            "AND b.category.id = :categoryId " +
            "AND (" +
            "   (b.startDate BETWEEN :start AND :end) OR " +
            "   (b.endDate BETWEEN :start AND :end) OR " +
            "   (:start BETWEEN b.startDate AND b.endDate)" +
            ")")
    boolean existOverlappingBudget(Long userId, Long categoryId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT COUNT(b) > 0 FROM Budget b " +
            "WHERE b.user.id = :userId " +
            "AND b.category.id = :categoryId " +
            "AND b.id != :excludedBudgetId " +
            "AND (" +
            "   (b.startDate BETWEEN :start AND :end) OR " +
            "   (b.endDate BETWEEN :start AND :end) OR " +
            "   (:start BETWEEN b.startDate AND b.endDate)" +
            ")")
    boolean existOverlappingBudgetExcludingCurrent(Long userId, Long categoryId, LocalDate startDate, LocalDate endDate, Long excludedBudgetId);


    boolean existsByUserIdAndCategoryIdAndStartDate(Long userId, Long categoryId, LocalDate startDate);

    @Modifying
    @Query("UPDATE Budget b SET b.category.id = :targetId " +
            "WHERE b.category.id = :sourceId AND b.user.id = :userId")
    void updateCategoryForBudgets(@Param("sourceId") Long sourceId, @Param("targetId") Long targetId, @Param("userId") Long userId);
}
