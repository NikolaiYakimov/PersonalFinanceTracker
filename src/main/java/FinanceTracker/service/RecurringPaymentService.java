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
import FinanceTracker.repository.CategoryRepository;
import FinanceTracker.repository.CurrencyRepository;
import FinanceTracker.repository.RecurringPaymentsRepository;
import FinanceTracker.repository.UserRepository;
import FinanceTracker.security.UserHelper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecurringPaymentService {

    private final RecurringPaymentsRepository recurringPaymentsRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final CurrencyRepository currencyRepository;
    private final RecurringPaymentMapper recurringPaymentMapper;
    private final UserHelper userHelper;

    private final TransactionService transactionService;

    public List<RecurringPaymentResponseDTO> getMyRecurringPayments() {
        User user = userHelper.getCurrentUser();
        return recurringPaymentsRepository.findByUserIdAndIsActiveTrue(user.getId())
                .stream()
                .map(recurringPaymentMapper::toDto)
                .toList();
    }

    @Transactional
    public RecurringPaymentResponseDTO createPayment(RecurringPaymentRequestDTO dto) {
        User user = userHelper.getCurrentUser();

        Category category = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        String code = (dto.currencyCode() != null) ? dto.currencyCode() : "EUR";
        Currency currency = currencyRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Currency not found"));

        if (recurringPaymentsRepository.existsByUserIdAndDescriptionIgnoreCaseAndIsActiveTrue(user.getId(), dto.description())) {
            throw new RuntimeException("Cannot have 2 recurring payments for same thing");
        }
        RecurringPayments recurringPayment = recurringPaymentMapper.toEntity(dto, user, category, currency);

        recurringPayment.setActive(true);
        if (recurringPayment.getNextRunDate() == null) {
            recurringPayment.setNextRunDate(dto.startDate());
        }

        RecurringPayments savedRecurringPayment = recurringPaymentsRepository.save(recurringPayment);
        return recurringPaymentMapper.toDto(savedRecurringPayment);
    }

    @Transactional
    public RecurringPaymentResponseDTO updatePayment(Long id, RecurringPaymentRequestDTO dto) {
        User user = userHelper.getCurrentUser();
        RecurringPayments recurringPayment = recurringPaymentsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (!recurringPayment.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized! You cannot update payment that is not yours!");
        }

        recurringPayment.setAmount(dto.amount());
        recurringPayment.setDescription(dto.description());
        recurringPayment.setFrequency(dto.frequency());

        if (!recurringPayment.getStartDate().isEqual(dto.startDate())) {
            recurringPayment.setStartDate(dto.startDate());
            recurringPayment.setNextRunDate(dto.startDate());
        }

        RecurringPayments savedRecurringPayment = recurringPaymentsRepository.save(recurringPayment);
        return recurringPaymentMapper.toDto(savedRecurringPayment);

    }

    public void toggleActiveStatus(Long id) {
        User user = userHelper.getCurrentUser();
        RecurringPayments recurringPayments = recurringPaymentsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (!recurringPayments.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized! Cannot changed status of payment of payment that is not yours!");
        }

        recurringPayments.setActive(!recurringPayments.isActive());
        recurringPaymentsRepository.save(recurringPayments);
    }

    @Transactional
    public void skipNextPayment(Long id) {
        User user = userHelper.getCurrentUser();
        RecurringPayments recurringPayment = recurringPaymentsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (!recurringPayment.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        LocalDateTime currentNextPaymentDate = recurringPayment.getNextRunDate();
        LocalDateTime newNextPaymentDate = updateNextDate(currentNextPaymentDate, recurringPayment.getFrequency());

        recurringPayment.setNextRunDate(newNextPaymentDate);
        recurringPaymentsRepository.save(recurringPayment);
    }

    public List<RecurringPaymentResponseDTO> getUpcomingPayments(int days) {
        User user = userHelper.getCurrentUser();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime end = now.plusDays(days);

        return recurringPaymentsRepository.findUpcomingPayments(user.getId(), now, end)
                .stream()
                .map(recurringPaymentMapper::toDto)
                .toList()
                ;
    }


    @Transactional
    public void deletePayment(Long id) {
        User user = userHelper.getCurrentUser();
        RecurringPayments recurringPayments = recurringPaymentsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recurring payment not found!"));

        if (!recurringPayments.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You cannot delete recurring payment that is not yours!");
        }
        recurringPaymentsRepository.delete(recurringPayments);
    }

    @Scheduled(cron = "0 0 8 * * ?")
    public void processDuePayments() {
        LocalDateTime now = LocalDateTime.now();
        log.info("Starting recurring payments check for date: {}", now);

        List<RecurringPayments> duePayments = recurringPaymentsRepository.findDuePayments(now);

        for (RecurringPayments payment : duePayments) {
            try {
                processSinglePayment(payment);
            } catch (Exception e) {
                // If payment of someone make error,we don't want to stop others , that's why we put Try-Catch is in the loop,!
                // Ако плащането на Иван гръмне, не искаме да спрем плащането на Мария.
                log.error("Failed to process payment ID: " + payment.getId(), e);
            }
        }
    }

    //TODO: Need to think about this with the UserHelper method, maybe will throw exception
    public void processSinglePayment(RecurringPayments payment) {
        TransactionRequestDTO transactionRequestDTO = new TransactionRequestDTO(
                payment.getAmount(),
                "Auto: " + payment.getDescription(),
                LocalDateTime.now(),
                payment.getCategory().getId(),
                payment.getCurrency().getCode()
        );

        transactionService.createTransaction(transactionRequestDTO);

        payment.setNextRunDate(updateNextDate(payment.getNextRunDate(), payment.getFrequency()));
        recurringPaymentsRepository.save(payment);

        log.info("Processed payment ID: {}", payment.getId());

    }

    private LocalDateTime updateNextDate(LocalDateTime current, Frequency frequency) {


        return switch (frequency) {
            case DAILY -> current.plusDays(1);
            case WEEKLY -> current.plusWeeks(1);
            case MONTHLY -> current.plusMonths(1);
            case QUARTERLY -> current.plusMonths(3);
            case YEARLY -> current.plusYears(1);
        };

    }

}
