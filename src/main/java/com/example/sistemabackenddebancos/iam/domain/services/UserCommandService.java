package com.example.sistemabackenddebancos.iam.domain.services;

import com.example.sistemabackenddebancos.iam.domain.model.commands.ChangePasswordCommand;
import com.example.sistemabackenddebancos.iam.domain.model.commands.LoginCommand;
import com.example.sistemabackenddebancos.iam.domain.model.commands.RegisterMfaMethodCommand;
import com.example.sistemabackenddebancos.iam.domain.model.commands.RegisterUserCommand;

public interface UserCommandService {
    void registerUser(RegisterUserCommand command);

    String login(LoginCommand command);

    void changePassword(ChangePasswordCommand command);

    void registerMfaMethod(RegisterMfaMethodCommand command);
}
