package com.example.sistemabackenddebancos.accounts.domain.services;

import com.example.sistemabackenddebancos.accounts.domain.model.aggregates.BankAccount;
import com.example.sistemabackenddebancos.accounts.domain.model.commands.*;

import java.util.Optional;

public interface AccountCommandService {

    Optional<BankAccount> handle(OpenAccountCommand command);

    Optional<BankAccount> handle(DepositCommand command);
    Optional<BankAccount> handle(WithdrawCommand command);

    Optional<BankAccount> handle(FreezeAccountCommand command);
    Optional<BankAccount> handle(UnfreezeAccountCommand command);

    Optional<BankAccount> handle(CloseAccountCommand command);
}