package my.project.dailybudget.entities.account;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import lombok.Data;
import lombok.NoArgsConstructor;
import my.project.library.dailybudget.enumerations.Color;
import my.project.library.dailybudget.enumerations.CurrencyCode;
import my.project.library.dailybudget.enumerations.ExpenseCategory;
import my.project.library.util.datetime.DateUtil;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@Entity(name = "expense_operations")
public class ExpenseOperation {

    @Id
    @SequenceGenerator(name = "expense_operation_id_sequence", sequenceName = "expense_operation_id_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "expense_operation_id_sequence")
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    @Digits(integer = 12, fraction = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    @Enumerated(EnumType.STRING)
    private CurrencyCode currencyCode;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account accountFrom;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private ExpenseCategory category;

    @Column(nullable = false)
    private OffsetDateTime timestamp;

    private String comment;

    public ExpenseOperation(Long userId,
                            BigDecimal amount,
                            CurrencyCode currencyCode,
                            Account accountFrom,
                            ExpenseCategory category,
                            String comment) {
        this.userId = userId;
        this.amount = amount;
        this.currencyCode = currencyCode;
        this.accountFrom = accountFrom;
        this.category = category;
        this.timestamp = DateUtil.nowInUtc();
        this.comment = comment;
    }
}
