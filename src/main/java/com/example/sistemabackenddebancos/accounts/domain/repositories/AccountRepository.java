package com.example.sistemabackenddebancos.accounts.domain.repositories;

import com.example.sistemabackenddebancos.accounts.domain.model.aggregates.BankAccount;
import com.example.sistemabackenddebancos.accounts.domain.model.valueobjects.AccountId;
import com.example.sistemabackenddebancos.accounts.domain.model.valueobjects.AccountNumber;
import com.example.sistemabackenddebancos.accounts.domain.model.valueobjects.OwnerId;

import java.util.List;
import java.util.Optional;

public interface AccountRepository {
    Optional<BankAccount> findById(AccountId id);
    Optional<BankAccount> findByAccountNumber(AccountNumber accountNumber);
    List<BankAccount> findAllByOwnerId(OwnerId ownerId);

    boolean existsByAccountNumber(AccountNumber accountNumber);

    BankAccount save(BankAccount account);
}