package com.example.sistemabackenddebancos.iam.domain.services;

import com.example.sistemabackenddebancos.iam.domain.model.readmodels.MfaMethodReadModel;
import com.example.sistemabackenddebancos.iam.domain.model.readmodels.UserReadModel;
import com.example.sistemabackenddebancos.iam.domain.model.valueobjects.Email;
import com.example.sistemabackenddebancos.iam.domain.model.valueobjects.UserId;

import java.util.List;

/**
 * Query Service del BC IAM.
 *
 * Solo LECTURA:
 * - No cambia estado
 * - No ejecuta reglas de negocio
 * - No devuelve agregados
 */
public interface UserQueryService {

    UserReadModel getUserById(UserId userId);

    UserReadModel getUserByEmail(Email email);

    List<MfaMethodReadModel> getMfaMethods(UserId userId);

    boolean isUserBlocked(UserId userId);
}