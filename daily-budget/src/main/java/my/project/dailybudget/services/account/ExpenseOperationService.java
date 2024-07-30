package my.project.dailybudget.services.account;

import lombok.RequiredArgsConstructor;
import my.project.dailybudget.entities.account.Account;
import my.project.dailybudget.entities.account.ExpenseOperation;
import my.project.dailybudget.entities.user.User;
import my.project.dailybudget.mappers.account.ExpenseOperationMapper;
import my.project.dailybudget.repositories.account.ExpenseOperationRepository;
import my.project.library.dailybudget.dtos.account.ExpenseOperationDto;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseOperationService {

    private final ExpenseOperationRepository expenseOperationRepository;
    private final ExpenseOperationMapper expenseOperationMapper;
    private final AccountService accountService;

    public List<ExpenseOperationDto> getAllExpenseOperationsByAccountId(Long accountId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<ExpenseOperation> allExpenseOperations =
                expenseOperationRepository.findAllByUserIdAndAccountFrom_IdOrderByTimestampDesc(user.getId(), accountId);

        return expenseOperationMapper.toDtoList(allExpenseOperations);
    }

    public void createExpenseOperation(ExpenseOperationDto dto) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Account account = accountService.getAccount(dto.accountFromId());

        ExpenseOperation expenseOperation = new ExpenseOperation(
                user.getId(),
                dto.amount(),
                dto.currencyCode(),
                account,
                dto.category(),
                dto.comment()
        );

        expenseOperationRepository.save(expenseOperation);
    }
}
