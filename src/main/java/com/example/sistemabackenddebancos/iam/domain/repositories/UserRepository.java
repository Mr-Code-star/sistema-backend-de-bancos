package com.example.sistemabackenddebancos.iam.domain.repositories;

import com.example.sistemabackenddebancos.iam.domain.model.aggregates.User;
import com.example.sistemabackenddebancos.iam.domain.model.valueobjects.Email;
import com.example.sistemabackenddebancos.iam.domain.model.valueobjects.UserId;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(UserId id);
    Optional<User> findByEmail(Email email);
    boolean existsByEmail(Email email);
    User save(User user);
}