package FinanceTracker.repository;

import FinanceTracker.entity.RecurringPayments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface RecurringPaymentsRepository extends JpaRepository<RecurringPayments, Long> {

    List<RecurringPayments> findByUserId(Long userId);

    List<RecurringPayments> findByUserIdAndAmount(Long userId, BigDecimal amount);

    //Find only active recurring payments for the user
    List<RecurringPayments> findByUserIdAndIsActiveIsTrue(Long userId);

    //Get all recurring payments that need to be done by the scheduler in the specific date
    @Query("SELECT r FROM RecurringPayments r " +
            "WHERE r.isActive = true " +
            "AND r.nextRunDate <= :now")
    List<RecurringPayments> findDuePayments(@Param("nextRunDate") LocalDateTime now);

    //Check if the user have current active payment for given service
    boolean existsByUserIdAndDescriptionAndIsActiveTrue(Long userId,String description);

}
