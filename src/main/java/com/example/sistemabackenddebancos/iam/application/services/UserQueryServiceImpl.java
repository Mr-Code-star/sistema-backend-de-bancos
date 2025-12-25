package com.example.sistemabackenddebancos.iam.application.services;

import com.example.sistemabackenddebancos.iam.domain.model.aggregates.User;
import com.example.sistemabackenddebancos.iam.domain.model.entities.MfaMethod;
import com.example.sistemabackenddebancos.iam.domain.model.enumerations.UserStatus;
import com.example.sistemabackenddebancos.iam.domain.model.queries.GetMfaMethodsQuery;
import com.example.sistemabackenddebancos.iam.domain.model.queries.GetUserByEmailQuery;
import com.example.sistemabackenddebancos.iam.domain.model.queries.GetUserByIdQuery;
import com.example.sistemabackenddebancos.iam.domain.model.queries.GetUserStatusQuery;
import com.example.sistemabackenddebancos.iam.domain.repositories.UserRepository;
import com.example.sistemabackenddebancos.iam.domain.services.UserQueryService;

import java.util.List;
import java.util.Optional;

public class UserQueryServiceImpl implements UserQueryService {

    private final UserRepository userRepository;

    public UserQueryServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> handle(GetUserByIdQuery query) {
        return userRepository.findById(query.userId());
    }

    @Override
    public Optional<User> handle(GetUserByEmailQuery query) {
        return userRepository.findByEmail(query.email());
    }

    @Override
    public Optional<UserStatus> handle(GetUserStatusQuery query) {
        return userRepository.findById(query.userId()).map(User::status);
    }

    @Override
    public List<MfaMethod> handle(GetMfaMethodsQuery query) {
        return userRepository.findById(query.userId())
                .map(User::mfaMethods)
                .orElse(List.of());
    }
}