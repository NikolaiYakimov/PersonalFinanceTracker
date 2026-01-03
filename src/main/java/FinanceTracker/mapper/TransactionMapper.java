package FinanceTracker.mapper;

import FinanceTracker.dto.TransactionRequestDTO;
import FinanceTracker.dto.TransactionResponseDTO;
import FinanceTracker.entity.Category;
import FinanceTracker.entity.Currency;
import FinanceTracker.entity.Transaction;
import FinanceTracker.entity.User;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public Transaction toEntity(TransactionRequestDTO dto, User user, Category category, Currency currency) {
        if(dto==null)
            throw new IllegalArgumentException("TransactionRequestDTO cannot be null");
        if(user==null)
            throw new IllegalArgumentException("User cannot be null");
        if(category==null)
            throw new IllegalArgumentException("Category cannot be null");
        if(currency==null)
            throw new IllegalArgumentException("Currency cannot be null");

        Transaction transaction=new Transaction();
        transaction.setAmount(dto.amount());
        transaction.setDescription(dto.description());
        transaction.setDate(dto.date());
        transaction.setUser(user);
        transaction.setCategory(category);
        transaction.setCurrency(currency);
        return transaction;

    }

    public TransactionResponseDTO toDto(Transaction transaction){
        if(transaction== null)
            throw new IllegalArgumentException("Transaction cannot be null");


        return new TransactionResponseDTO(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getDate(),
                transaction.getCategory().getType(),
                transaction.getCategory().getId(),
                transaction.getCategory().getName(),
                transaction.getCurrency().getCode(),
                transaction.getCurrency().getSymbol()
        );
    }
}
