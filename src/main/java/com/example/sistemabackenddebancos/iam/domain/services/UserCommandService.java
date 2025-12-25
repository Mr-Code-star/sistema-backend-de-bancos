package com.example.sistemabackenddebancos.iam.domain.services;

import com.example.sistemabackenddebancos.iam.domain.model.aggregates.User;
import com.example.sistemabackenddebancos.iam.domain.model.commands.*;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.Optional;

/**
 * User command service
 *
 * This interface represents the service to handle user commands (CQRS – Write side).
 */
public interface UserCommandService {

    /**
     * Handle user login
     *
     * @param command the {@link LoginCommand}
     * @return Optional pair of authenticated {@link User} and JWT token
     */
    Optional<ImmutablePair<User, String>> handle(LoginCommand command);

    /**
     * Handle user registration
     *
     * @param command the {@link RegisterUserCommand}
     * @return Optional of created {@link User}
     */
    Optional<User> handle(RegisterUserCommand command);

    /**
     * Handle password change
     *
     * @param command the {@link ChangePasswordCommand}
     * @return Optional of updated {@link User}
     */
    Optional<User> handle(ChangePasswordCommand command);

    /**
     * Handle MFA method registration
     *
     * @param command the {@link RegisterMfaMethodCommand}
     * @return Optional of updated {@link User}
     */
    Optional<User> handle(RegisterMfaMethodCommand command);
}
