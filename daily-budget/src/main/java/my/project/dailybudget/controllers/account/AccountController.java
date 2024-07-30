package my.project.dailybudget.controllers.account;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import my.project.dailybudget.services.account.AccountService;
import my.project.library.dailybudget.dtos.account.AccountDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public ResponseEntity<List<AccountDto>> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    @PostMapping
    public ResponseEntity<Void> createAccount(@RequestBody @Valid AccountDto accountDto) {
        accountService.createAccount(accountDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/{accountId}")
    public ResponseEntity<AccountDto> updateAccount(@PathVariable("accountId") Long accountId,
                                                    @RequestBody @Valid AccountDto accountDto) {
        return ResponseEntity.status(HttpStatus.OK).body(accountService.updateAccount(accountId, accountDto));
    }
}
