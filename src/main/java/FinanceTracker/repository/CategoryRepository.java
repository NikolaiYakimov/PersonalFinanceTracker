package FinanceTracker.repository;

import FinanceTracker.entity.Category;
import FinanceTracker.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByUserId(Long id);

    Optional<Category> findByNameAndUserIsNull(String name);

    Optional<Category> findByNameAndUserId(String name, Long userId);

    @Query("SELECT c FROM Category c WHERE c.type = :type AND (c.user.id = :userId OR c.user IS NULL)")
    List<Category> findByTypeAndUser(@Param("type") TransactionType type, @Param("userId") Long userId);

    @Query("SELECT c FROM Category c WHERE c.user.id= :userId OR c.user IS NULL ORDER BY c.name ASC")
    List<Category> findAllBaseAndUserCategories(@Param("userId") Long userId);

    @Query("SELECT c FROM Category c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) AND (c.user.id = :userId OR c.user IS NULL)")
    List<Category> searchByName(@Param("keyword") String keyword, @Param("userId") Long userId);

    @Query("SELECT c FROM Category c " +
            "WHERE (c.user.id = :userId OR c.user IS NULL) " +
            "AND c.type = :type")
    List<Category> findAllBaseAndUserCategoriesByType(@Param("userId") Long userId, @Param("type") TransactionType type);

    boolean existsByNameAndUserIsNull(String name);

    boolean existsByNameAndUserId(String name, Long userId);

    //Check for duplicates except current category we gave him
    @Query("SELECT COUNT(c) > 0 FROM Category c " +
            "WHERE c.name = :name " +
            "AND c.user.id = :userId " +
            "AND c.id != :excludedId")
    boolean existsByNameAndUserIdExcludingCurrent(@Param("name") String name, @Param("userId") Long userId, @Param("excludedId") Long excludedId);

}
