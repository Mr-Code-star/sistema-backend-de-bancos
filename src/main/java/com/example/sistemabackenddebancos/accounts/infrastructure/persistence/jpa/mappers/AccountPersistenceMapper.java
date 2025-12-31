package com.example.sistemabackenddebancos.accounts.infrastructure.persistence.jpa.mappers;


import com.example.sistemabackenddebancos.accounts.domain.model.aggregates.BankAccount;
import com.example.sistemabackenddebancos.accounts.domain.model.valueobjects.AccountId;
import com.example.sistemabackenddebancos.accounts.domain.model.valueobjects.AccountNumber;
import com.example.sistemabackenddebancos.shared.domain.model.valueobjects.Money;
import com.example.sistemabackenddebancos.accounts.domain.model.valueobjects.OwnerId;
import com.example.sistemabackenddebancos.accounts.infrastructure.persistence.jpa.entities.AccountEntity;

public class AccountPersistenceMapper {

    public static BankAccount toDomain(AccountEntity e) {
        return new BankAccount(
                new AccountId(e.getId()),
                new OwnerId(e.getOwnerId()),
                new AccountNumber(e.getAccountNumber()),
                e.getType(),
                e.getStatus(),
                e.getCurrency(),
                new Money(e.getBalance(), e.getCurrency())
        );
    }

    public static AccountEntity toEntity(BankAccount a) {
        var e = new AccountEntity();
        e.setId(a.id().value());
        e.setOwnerId(a.ownerId().value());
        e.setAccountNumber(a.accountNumber().value());
        e.setType(a.type());
        e.setStatus(a.status());
        e.setCurrency(a.currency());
        e.setBalance(a.balance().amount());
        return e;
    }
}