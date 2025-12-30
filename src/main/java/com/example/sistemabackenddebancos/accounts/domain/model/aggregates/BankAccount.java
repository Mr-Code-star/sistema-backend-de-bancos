package com.example.sistemabackenddebancos.accounts.domain.model.aggregates;

import com.example.sistemabackenddebancos.accounts.domain.model.enumerations.AccountStatus;
import com.example.sistemabackenddebancos.accounts.domain.model.enumerations.AccountType;
import com.example.sistemabackenddebancos.accounts.domain.model.enumerations.Currency;
import com.example.sistemabackenddebancos.accounts.domain.model.valueobjects.AccountId;
import com.example.sistemabackenddebancos.accounts.domain.model.valueobjects.AccountNumber;
import com.example.sistemabackenddebancos.accounts.domain.model.valueobjects.Money;
import com.example.sistemabackenddebancos.accounts.domain.model.valueobjects.OwnerId;

import java.math.BigDecimal;
import java.util.Objects;

public class BankAccount {
    private final AccountId id;
    private final OwnerId ownerId;
    private final AccountNumber accountNumber;

    private final AccountType type;
    private final AccountStatus status;

    private final Currency currency;
    private final Money balance;

    public BankAccount (AccountId id,
                         OwnerId ownerId,
                         AccountNumber accountNumber,
                         AccountType type,
                         AccountStatus status,
                         Currency currency,
                         Money balance) {
        this.id = Objects.requireNonNull(id, "AccountId cannot be null");
        this.ownerId = Objects.requireNonNull(ownerId, "OwnerId cannot be null");
        this.accountNumber = Objects.requireNonNull(accountNumber, "AccountNumber cannot be null");
        this.type = Objects.requireNonNull(type, "AccountType cannot be null");
        this.status = Objects.requireNonNull(status, "AccountStatus cannot be null");
        this.currency = Objects.requireNonNull(currency, "Currency cannot be null");
        this.balance = Objects.requireNonNull(balance, "Balance cannot be null");

        if (balance.currency() != currency) {
            throw new IllegalArgumentException("Balance currency must match account currency");
        }
    }

    public static BankAccount open(OwnerId ownerId, AccountNumber accountNumber, AccountType type, Currency currency) {
        return new BankAccount(
                AccountId.newId(),
                ownerId,
                accountNumber,
                type,
                AccountStatus.ACTIVE,
                currency,
                Money.zero(currency)
        );
    }

    public AccountId id() { return id; }
    public OwnerId ownerId() { return ownerId; }
    public AccountNumber accountNumber() { return accountNumber; }
    public AccountType type() { return type; }
    public AccountStatus status() { return status; }
    public Currency currency() { return currency; }
    public Money balance() { return balance; }

    // Metodos de reglas de negocio

    // depositar
    public BankAccount deposit(BigDecimal amount) {
        ensureActive();
        var money = new Money(amount, currency);
        if(money.amount().compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("Deposit amount must be > 0");
        }
        return new BankAccount(id, ownerId, accountNumber, type, status, currency, balance.add(money));
    }

    // Retirar
    public BankAccount withdraw(BigDecimal amount) {
        ensureActive();
        var money = new Money(amount, currency);
        if (money.amount().compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("Withdraw amount must be > 0");
        }
        return new BankAccount(id, ownerId, accountNumber, type, status, currency, balance.subtract(money));
    }

    public BankAccount freeze() {
        if (status == AccountStatus.CLOSED) throw new IllegalStateException("Cannot freeze a closed account");
        if (status == AccountStatus.FROZEN) return this;
        return new BankAccount(id, ownerId, accountNumber, type, AccountStatus.FROZEN, currency, balance);
    }

    public BankAccount unfreeze() {
        if (status == AccountStatus.CLOSED) throw new IllegalStateException("Cannot unfreeze a closed account");
        if (status == AccountStatus.ACTIVE) return this;
        return new BankAccount(id, ownerId, accountNumber, type, AccountStatus.ACTIVE, currency, balance);
    }

    public BankAccount close() {
        if (status == AccountStatus.CLOSED) return this;
        if (balance.amount().compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalStateException("Cannot close account with non-zero balance");
        }
        return new BankAccount(id, ownerId, accountNumber, type, AccountStatus.CLOSED, currency, balance);
    }


    private void ensureActive() {
        if (status == AccountStatus.CLOSED) throw new IllegalStateException("Account is CLOSED");
        if (status == AccountStatus.FROZEN) throw new IllegalStateException("Account is FROZEN");
    }

}
