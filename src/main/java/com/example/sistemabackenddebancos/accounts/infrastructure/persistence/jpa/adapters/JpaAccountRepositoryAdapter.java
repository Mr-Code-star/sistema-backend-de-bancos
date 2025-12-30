package com.example.sistemabackenddebancos.accounts.infrastructure.persistence.jpa.adapters;

import com.example.sistemabackenddebancos.accounts.domain.model.aggregates.BankAccount;
import com.example.sistemabackenddebancos.accounts.domain.model.valueobjects.AccountId;
import com.example.sistemabackenddebancos.accounts.domain.model.valueobjects.AccountNumber;
import com.example.sistemabackenddebancos.accounts.domain.model.valueobjects.OwnerId;
import com.example.sistemabackenddebancos.accounts.domain.repositories.AccountRepository;
import com.example.sistemabackenddebancos.accounts.infrastructure.persistence.jpa.mappers.AccountPersistenceMapper;
import com.example.sistemabackenddebancos.accounts.infrastructure.persistence.jpa.repositories.SpringDataAccountJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaAccountRepositoryAdapter implements AccountRepository {

    private final SpringDataAccountJpaRepository jpa;

    public JpaAccountRepositoryAdapter(SpringDataAccountJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<BankAccount> findById(AccountId id) {
        return jpa.findById(id.value()).map(AccountPersistenceMapper::toDomain);
    }

    @Override
    public Optional<BankAccount> findByAccountNumber(AccountNumber accountNumber) {
        return jpa.findByAccountNumber(accountNumber.value()).map(AccountPersistenceMapper::toDomain);
    }

    @Override
    public List<BankAccount> findAllByOwnerId(OwnerId ownerId) {
        return jpa.findAllByOwnerId(ownerId.value())
                .stream()
                .map(AccountPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByAccountNumber(AccountNumber accountNumber) {
        return jpa.existsByAccountNumber(accountNumber.value());
    }

    @Override
    public BankAccount save(BankAccount account) {
        var saved = jpa.save(AccountPersistenceMapper.toEntity(account));
        return AccountPersistenceMapper.toDomain(saved);
    }
}