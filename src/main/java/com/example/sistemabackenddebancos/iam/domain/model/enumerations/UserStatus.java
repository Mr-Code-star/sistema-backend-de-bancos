package com.example.sistemabackenddebancos.iam.domain.model.enumerations;

/**
 * User Status
 * <text>este enum representa el estado de acceso del usuario</text>
 * <ul>
 *     <li> Pending -> registado pero no confirmado</li>
 *     <li> Active -> puede auntenticarse</li>
 *     <li> Blocked -> acceso denegado (intentos fallidos, fraudes, admin)</li>
 * </ul>
 * */

public enum UserStatus {
    PENDING,
    ACTIVE,
    BLOCKED
}
