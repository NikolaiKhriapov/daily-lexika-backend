package my.project.library.dailybudget.dtos.account;

import my.project.library.dailybudget.enumerations.Color;
import my.project.library.dailybudget.enumerations.CurrencyCode;
import my.project.library.dailylexika.enumerations.LogAction;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Currency;

public record AccountDto(

    Long id,
    Long userId,
    String name,
    BigDecimal amount,
    CurrencyCode currencyCode,
    Color color,
    Boolean isActive

) implements Serializable {
}
