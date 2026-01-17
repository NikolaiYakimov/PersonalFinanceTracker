package FinanceTracker.controller;

import FinanceTracker.dto.RecurringPaymentRequestDTO;
import FinanceTracker.dto.RecurringPaymentResponseDTO;
import FinanceTracker.service.RecurringPaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/api/recurring")
@RequiredArgsConstructor
public class RecurringPaymentController {

    private final RecurringPaymentService recurringPaymentService;

    @GetMapping
    public ResponseEntity<List<RecurringPaymentResponseDTO>> getMyRecurringPayments(){
        return ResponseEntity.ok(recurringPaymentService.getMyRecurringPayments());
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<RecurringPaymentResponseDTO>> getUpcomingPayments(
            @RequestParam(defaultValue = "7") int days
    ){
        return ResponseEntity.ok(recurringPaymentService.getUpcomingPayments(days));
    }

    @PostMapping
    public ResponseEntity<RecurringPaymentResponseDTO> createPayment(
            @RequestBody @Valid RecurringPaymentRequestDTO dto){

        return ResponseEntity.ok(recurringPaymentService.createPayment(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecurringPaymentResponseDTO> updatePayment(
            @PathVariable Long id,
            @RequestBody @Valid RecurringPaymentRequestDTO dto
    ){
        return ResponseEntity.ok(recurringPaymentService.updatePayment(id,dto));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<Void> toggleActiveStatus(@PathVariable Long id){
        recurringPaymentService.toggleActiveStatus(id);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/{id}/skip")
    public ResponseEntity<Void> skipNextPayment(@PathVariable Long id){
        recurringPaymentService.skipNextPayment(id);
        return ResponseEntity.ok().build();

    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        recurringPaymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }

}
