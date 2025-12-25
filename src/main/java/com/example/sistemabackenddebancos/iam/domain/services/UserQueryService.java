package com.example.sistemabackenddebancos.iam.domain.services;

import com.example.sistemabackenddebancos.iam.domain.model.aggregates.User;
import com.example.sistemabackenddebancos.iam.domain.model.entities.MfaMethod;
import com.example.sistemabackenddebancos.iam.domain.model.enumerations.UserStatus;
import com.example.sistemabackenddebancos.iam.domain.model.queries.GetMfaMethodsQuery;
import com.example.sistemabackenddebancos.iam.domain.model.queries.GetUserByEmailQuery;
import com.example.sistemabackenddebancos.iam.domain.model.queries.GetUserByIdQuery;
import com.example.sistemabackenddebancos.iam.domain.model.queries.GetUserStatusQuery;


import java.util.List;
import java.util.Optional;

/**
 * Query Service del BC IAM.
 *
 * Solo LECTURA:
 * - No cambia estado
 * - No ejecuta reglas de negocio
 * - No devuelve agregados
 */
/**
 * Query Service: solo lectura.
 */
public interface UserQueryService {

    /** Obtener usuario por ID */
    Optional<User> handle(GetUserByIdQuery query);

    /** Obtener usuario por email */
    Optional<User> handle(GetUserByEmailQuery query);

    /** Obtener estado del usuario (ACTIVE / PENDING / BLOCKED) */
    Optional<UserStatus> handle(GetUserStatusQuery query);

    /** Listar métodos MFA registrados */
    List<MfaMethod> handle(GetMfaMethodsQuery query);
}