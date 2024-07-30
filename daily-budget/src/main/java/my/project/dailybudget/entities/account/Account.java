package my.project.dailybudget.entities.account;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import lombok.Data;
import lombok.NoArgsConstructor;
import my.project.library.dailybudget.enumerations.Color;
import my.project.library.dailybudget.enumerations.CurrencyCode;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Entity(name = "accounts")
public class Account {

    @Id
    @SequenceGenerator(name = "account_id_sequence", sequenceName = "account_id_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_id_sequence")
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false)
    @Digits(integer = 12, fraction = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    @Enumerated(EnumType.STRING)
    private CurrencyCode currencyCode;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Color color;

    @Column(nullable = false)
    private Boolean isActive;

    public Account(Long userId, String name, BigDecimal amount, CurrencyCode currencyCode, Color color) {
        this.userId = userId;
        this.name = name;
        this.amount = amount;
        this.currencyCode = currencyCode;
        this.color = color;
        this.isActive = true;
    }
}
