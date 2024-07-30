package my.project.dailybudget.services.account;

import lombok.RequiredArgsConstructor;
import my.project.dailybudget.config.i18n.I18nUtil;
import my.project.dailybudget.entities.account.Account;
import my.project.dailybudget.entities.user.User;
import my.project.dailybudget.mappers.account.AccountMapper;
import my.project.dailybudget.repositories.account.AccountRepository;
import my.project.library.dailybudget.dtos.account.AccountDto;
import my.project.library.dailybudget.enumerations.CurrencyCode;
import my.project.library.util.exception.ResourceNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    public List<AccountDto> getAllAccounts() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Account> allAccounts = accountRepository.findAllByUserId(user.getId());
        return accountMapper.toDtoList(allAccounts);
    }

    public void createAccount(AccountDto accountDto) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Account account = new Account(
                user.getId(),
                accountDto.name(),
                accountDto.amount() != null ? accountDto.amount() : BigDecimal.ZERO,
                accountDto.currencyCode(),
                accountDto.color()
        );

        accountRepository.save(account);
    }

    public AccountDto updateAccount(Long accountId, AccountDto accountDto) {
        Account account = getAccount(accountId);

        account.setName(accountDto.name());
        account.setAmount(accountDto.amount());
        account.setCurrencyCode(accountDto.currencyCode());
        account.setColor(accountDto.color());

        Account updatedAccount = accountRepository.save(account);

        return accountMapper.toDto(updatedAccount);
    }

    public Account getAccount(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException(I18nUtil.getMessage("dailybudget-exceptions.account.notFound")));
    }
}
