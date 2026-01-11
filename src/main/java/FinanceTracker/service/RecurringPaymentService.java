package FinanceTracker.service;

import FinanceTracker.dto.RecurringPaymentRequestDTO;
import FinanceTracker.dto.RecurringPaymentResponseDTO;
import FinanceTracker.dto.TransactionRequestDTO;
import FinanceTracker.entity.Category;
import FinanceTracker.entity.Currency;
import FinanceTracker.entity.RecurringPayments;
import FinanceTracker.entity.User;
import FinanceTracker.enums.Frequency;
import FinanceTracker.mapper.RecurringPaymentMapper;
import FinanceTracker.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecurringPaymentService {

    private final RecurringPaymentsRepository recurringPaymentsRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final CurrencyRepository currencyRepository;
    private final RecurringPaymentMapper recurringPaymentMapper;

    private final TransactionService transactionService;

    public List<RecurringPaymentResponseDTO> getMyRecurringPayments(Long userId){
        return recurringPaymentsRepository.findByUserIdAndIsActiveTrue(userId).stream()
                .map(recurringPaymentMapper::toDto)
                .toList();
    }

    @Transactional
    public void createPayment(RecurringPaymentRequestDTO dto,Long userId){
        User user=userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Category category=categoryRepository.findById(dto.categoryId())
                .orElseThrow(()->new RuntimeException("Category not found"));

        String code=(dto.currencyCode()!=null) ? dto.currencyCode():"EUR";
        Currency currency=currencyRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Currency not found"));

        if (recurringPaymentsRepository.existsByUserIdAndDescriptionIgnoreCaseAndIsActiveTrue(userId,dto.description()))
        {
            throw new RuntimeException("Cannot have 2 recurring payments for same thing");
        }
        RecurringPayments recurringPayments=recurringPaymentMapper.toEntity(dto,user,category,currency);
        recurringPaymentsRepository.save(recurringPayments);
    }

    //TODO : Need to think about the logic for deleting(only unActive ones or from all)
    @Transactional
    public void delete(Long id,Long userId){
        RecurringPayments recurringPayments=recurringPaymentsRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Recurring payment not found!"));

        if(!recurringPayments.getUser().getId().equals(userId))
        {
            throw new RuntimeException("You cannot delete recurring payment that is not yours!");
        }
        recurringPaymentsRepository.delete(recurringPayments);
    }

    @Scheduled (cron = "0 0 8 * * ?")
    @Transactional
    public void processDuePayments(){
        LocalDateTime now=LocalDateTime.now();
        List<RecurringPayments> duePayments=recurringPaymentsRepository.findDuePayments(now);

        for (RecurringPayments payments: duePayments)
        {
            TransactionRequestDTO toDto=new TransactionRequestDTO(
                    payments.getAmount(),
                    "Auto payment: " + payments.getDescription(),
                    now,
                    payments.getCategory().getId(),
                    payments.getCurrency().getCode()
                    );
            transactionService.createTransaction(toDto,payments.getUser().getId());
            updateNextDate(payments);
            recurringPaymentsRepository.save(payments);
        }
    }


    //TODO Need to add method for unActive recurring payment

    private void updateNextDate(RecurringPayments payments){
        LocalDateTime cur=payments.getNextRunDate();
        Frequency frequency=payments.getFrequency();

        LocalDateTime nextPaymentDate=switch (frequency){
            case DAILY -> cur.plusDays(1);
            case WEEKLY -> cur.plusWeeks(1);
            case MONTHLY -> cur.plusMonths(1);
            case QUARTERLY -> cur.plusMonths(3);
            case YEARLY -> cur.plusYears(1);
        };
        payments.setNextRunDate(nextPaymentDate);
    }

}
