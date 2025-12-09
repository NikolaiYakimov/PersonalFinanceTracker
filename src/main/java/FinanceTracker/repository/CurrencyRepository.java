package FinanceTracker.repository;

import FinanceTracker.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CurrencyRepository extends JpaRepository<Currency,Long> {

//Search by currency code
    Optional<Currency> findByCode(String code);

    boolean existByCode(String code);
}

