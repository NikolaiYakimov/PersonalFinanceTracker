package FinanceTracker.service;

import FinanceTracker.dto.TransactionRequestDTO;
import FinanceTracker.dto.TransactionResponseDTO;
import FinanceTracker.entity.Category;
import FinanceTracker.entity.Currency;
import FinanceTracker.entity.Transaction;
import FinanceTracker.entity.User;
import FinanceTracker.mapper.TransactionMapper;
import FinanceTracker.repository.CategoryRepository;
import FinanceTracker.repository.CurrencyRepository;
import FinanceTracker.repository.TransactionRepository;
import FinanceTracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final TransactionMapper transactionMapper;
    private final CurrencyRepository currencyRepository;

    @Transactional
    public void createTransaction(TransactionRequestDTO transactionDTO, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Category category = categoryRepository.findById(transactionDTO.categoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));


        //If category have owner , and the owner is not you
        if (category.getUser() != null && !category.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Cannot make transaction to categories that is not yours");
        }

        String code = (transactionDTO.currencyCode() != null) ? transactionDTO.currencyCode() : "EUR";
        Currency currency=currencyRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Currency not found: "+ code));

        Transaction transaction=transactionMapper.toEntity(transactionDTO,user,category,currency);
        transactionRepository.save(transaction);
    }

    public List<TransactionResponseDTO> getMyTransactions(Long userId){

        return transactionRepository.findByUserIdOrderByDateDesc(userId).stream()
                .map(transactionMapper::toDto)
                .toList();
    }

    @Transactional
    public void  deleteTransaction(Long transactionId, Long userId) {
        Transaction transaction=transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if(!transaction.getUser().getId().equals(userId)){
            throw new RuntimeException("Cannot delete transaction that is not yours");
        }

         transactionRepository.delete(transaction);
    }


}
