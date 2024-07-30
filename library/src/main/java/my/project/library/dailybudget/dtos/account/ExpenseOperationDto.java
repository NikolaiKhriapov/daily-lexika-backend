package my.project.library.dailybudget.dtos.account;

import my.project.library.dailybudget.enumerations.CurrencyCode;
import my.project.library.dailybudget.enumerations.ExpenseCategory;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record ExpenseOperationDto(

    Long id,
    Long userId,
    BigDecimal amount,
    CurrencyCode currencyCode,
    Long accountFromId,
    ExpenseCategory category,
    OffsetDateTime timestamp,
    String comment

) implements Serializable {
}
