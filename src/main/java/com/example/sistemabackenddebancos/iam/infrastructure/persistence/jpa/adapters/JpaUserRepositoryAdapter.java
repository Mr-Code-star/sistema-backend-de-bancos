package com.example.sistemabackenddebancos.iam.infrastructure.persistence.jpa.adapters;

import com.example.sistemabackenddebancos.iam.domain.model.aggregates.User;
import com.example.sistemabackenddebancos.iam.domain.model.valueobjects.Email;
import com.example.sistemabackenddebancos.iam.domain.model.valueobjects.UserId;
import com.example.sistemabackenddebancos.iam.domain.repositories.UserRepository;
import com.example.sistemabackenddebancos.iam.infrastructure.persistence.jpa.mappers.UserPersistenceMapper;
import com.example.sistemabackenddebancos.iam.infrastructure.persistence.jpa.repositories.SpringDataUserJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaUserRepositoryAdapter implements UserRepository {

    private final SpringDataUserJpaRepository jpa;

    public JpaUserRepositoryAdapter(SpringDataUserJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<User> findById(UserId id) {
        return jpa.findById(id.value()).map(UserPersistenceMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return jpa.findByEmail(email.value()).map(UserPersistenceMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return jpa.existsByEmail(email.value());
    }

    @Override
    public User save(User user) {
        var entity = UserPersistenceMapper.toEntity(user);
        var saved = jpa.save(entity);
        return UserPersistenceMapper.toDomain(saved);
    }
}