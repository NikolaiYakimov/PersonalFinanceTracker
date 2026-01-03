package FinanceTracker.mapper;

import FinanceTracker.dto.RecurringPaymentRequestDTO;
import FinanceTracker.dto.RecurringPaymentResponseDTO;
import FinanceTracker.entity.Category;
import FinanceTracker.entity.Currency;
import FinanceTracker.entity.RecurringPayments;
import FinanceTracker.entity.User;
import org.springframework.stereotype.Component;

@Component
public class RecurringPaymentMapper {

    public RecurringPayments toEntity(RecurringPaymentRequestDTO dto, User user, Category category, Currency currency){
        if (dto == null)
            throw new IllegalArgumentException("RecurringPaymentRequestDTO cannot be null");

        if (user == null)
            throw new IllegalArgumentException("User cannot be null");

        if (category == null)
            throw new IllegalArgumentException("Category cannot be null");

        RecurringPayments payment= new RecurringPayments();
        payment.setAmount(dto.amount());
        payment.setDescription(dto.description());
        payment.setStartDate(dto.startDate());
        payment.setNextRunDate(dto.startDate());
        payment.setActive(true);
        payment.setUser(user);
        payment.setCategory(category);
        payment.setCurrency(currency);

        return payment;
    }

    public RecurringPaymentResponseDTO toDto(RecurringPayments payment){
        if(payment==null)
            throw new IllegalArgumentException("Payment cannot be null");

        return new RecurringPaymentResponseDTO(
                payment.getId(),
                payment.getAmount(),
                payment.getDescription(),
                payment.getCategory().getName(),
                payment.getNextRunDate().toLocalDate(),
                payment.isActive(),
                payment.getCurrency().getCode(),
                payment.getCurrency().getSymbol()
        );

    }
}
