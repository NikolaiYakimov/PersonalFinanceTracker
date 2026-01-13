package FinanceTracker.repository;

import FinanceTracker.entity.RecurringPayments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface RecurringPaymentsRepository extends JpaRepository<RecurringPayments, Long> {

    List<RecurringPayments> findByUserId(Long userId);

    //Find only active recurring payments for the user
    List<RecurringPayments> findByUserIdAndIsActiveTrue(Long userId);

    List<RecurringPayments> findByUserIdAndAmount(Long userId, BigDecimal amount);

    //Search for  subscriptions that is greater than some amount we give
    List<RecurringPayments> findByUserIdAndAmountGreaterThan(Long userId,BigDecimal amount);

    List<RecurringPayments> findByUserIdAndAmountBetween(Long userId,BigDecimal minAmount,BigDecimal maxAmount);

    //Get all recurring payments that need to be done by the scheduler in the specific date
    @Query("SELECT r FROM RecurringPayments r " +
            "WHERE r.isActive = true " +
            "AND r.nextRunDate <= :now")
    List<RecurringPayments> findDuePayments(@Param("now") LocalDateTime now);

    @Query("SELECT r FROM RecurringPayments r " +
            "WHERE r.user.id = :userId " +
            "AND r.isActive = true " +
            "AND r.nextRunDate BETWEEN :start AND :end " +
            "ORDER BY r.nextRunDate ASC")
    List<RecurringPayments> findUpcomingPayments(@Param("userId") Long userId,@Param("start") LocalDateTime start,@Param("end") LocalDateTime end);

    //Check if the user have current active payment for given service
    boolean existsByUserIdAndDescriptionIgnoreCaseAndIsActiveTrue(Long userId,String description);

    @Modifying
    @Query("UPDATE RecurringPayments r SET r.category.id = :targetId " +
            "WHERE r.category.id = :sourceId AND r.user.id = :userId")
    void updateCategoryForRecurringPayments(@Param("sourceId") Long sourceId, @Param("targetId") Long targetId, @Param("userId") Long userId);

}
