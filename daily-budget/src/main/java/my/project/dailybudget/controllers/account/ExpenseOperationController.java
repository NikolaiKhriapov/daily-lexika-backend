package my.project.dailybudget.controllers.account;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import my.project.dailybudget.services.account.ExpenseOperationService;
import my.project.library.dailybudget.dtos.account.ExpenseOperationDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/expense-operations")
@RequiredArgsConstructor
public class ExpenseOperationController {

    private final ExpenseOperationService expenseOperationService;

    @GetMapping("/by-account/{accountId}")
    public ResponseEntity<List<ExpenseOperationDto>> getAllExpenseOperationsByAccountId(@PathVariable("accountId") Long accountId) {
        return ResponseEntity.ok(expenseOperationService.getAllExpenseOperationsByAccountId(accountId));
    }

    @PostMapping
    public ResponseEntity<Void> createExpenseOperation(@RequestBody @Valid ExpenseOperationDto expenseOperationDto) {
        expenseOperationService.createExpenseOperation(expenseOperationDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
