package FinanceTracker.mapper;

import FinanceTracker.dto.CurrencyResponseDTO;
import FinanceTracker.entity.Currency;
import org.springframework.stereotype.Component;

@Component
public class CurrencyMapper {

    public CurrencyResponseDTO toDto(Currency currency) {
        if(currency == null) {
            return null;
        }
        return new CurrencyResponseDTO(
                currency.getId(),
                currency.getCode(),
                currency.getSymbol()
        );
    }
}
