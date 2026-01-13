package FinanceTracker.repository;

import FinanceTracker.dto.CategorySumDTO;
import FinanceTracker.entity.Transaction;
import FinanceTracker.enums.TransactionType;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUserIdOrderByDateDesc(Long userid);

    Page<Transaction> findByUserIdOrderByDateDesc(Long userid, Pageable pageable);


    List<Transaction> findByUserIdAndCategoryIdOrderByDateDesc(Long userId, Long categoryId);

    List<Transaction> findByUserIdAndDateBetweenOrderByDateDesc(Long userId, LocalDateTime startDate, LocalDateTime endDate);

    List<Transaction> findByUserIdAndCategory_Type(Long userId, TransactionType type);

    List<Transaction> findByUserIdAndDescriptionContainingIgnoreCase(Long userId, String description);

    @Query("SELECT c.name as categoryName, SUM(t.amount) as totalAmount " +
            "FROM Transaction t JOIN t.category c " +
            "WHERE t.user.id = :userId AND c.type = :type " +
            "GROUP BY c.name")
    List<CategorySumDTO> findGroupByCategory(@Param("userId") Long userId,
                                             @Param("type") TransactionType type);

    @Query("SELECT SUM(t.amount) FROM Transaction t " +
            "WHERE t.user.id= :userId AND t.category.type=:type")
    BigDecimal sumTotalAmountByType(@Param("userId") Long userId,
                                    @Param("type") TransactionType type);

    @Query("SELECT SUM(t.amount) FROM Transaction t " +
            "WHERE t.user.id = :userId " +
            "AND t.category.type = :type " +
            "AND t.date BETWEEN :startDate AND :endDate")
    BigDecimal sumTotalAmountByTypeAndDate(@Param("userId") Long userId,
                                           @Param("type") TransactionType type,
                                           @Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);


    //Calculates the total accumulated amount for a specific category(Spent vs. Earned)
    @Query("SELECT SUM(t.amount) FROM Transaction t " +
            "WHERE t.user.id = :userId AND t.category.id = :categoryId ")
    BigDecimal sumTotalByCategoryId(@Param("userId") Long userId,
                                    @Param("categoryId") Long categoryId);

    //Calculates the total accumulated amount for a specific category(Spent vs. Earned) for a month
    @Query("SELECT SUM(t.amount) FROM Transaction t " +
            "WHERE t.user.id= :userId " +
            "AND t.category.id= :categoryId " +
            "AND t.date BETWEEN: startDate AND :endDate")
    BigDecimal sumTotalByCategoryIdAndDate(@Param("userId") Long userId,
                                           @Param("categoryId") Long categoryId,
                                           @Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);


    boolean existsByCategoryId(Long categoryId);
}
