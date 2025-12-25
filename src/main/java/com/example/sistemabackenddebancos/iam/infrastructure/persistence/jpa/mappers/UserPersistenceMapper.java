package com.example.sistemabackenddebancos.iam.infrastructure.persistence.jpa.mappers;

import com.example.sistemabackenddebancos.iam.domain.model.aggregates.User;
import com.example.sistemabackenddebancos.iam.domain.model.entities.MfaMethod;
import com.example.sistemabackenddebancos.iam.domain.model.valueobjects.Email;
import com.example.sistemabackenddebancos.iam.domain.model.valueobjects.MfaMethodId;
import com.example.sistemabackenddebancos.iam.domain.model.valueobjects.PasswordHash;
import com.example.sistemabackenddebancos.iam.domain.model.valueobjects.UserId;
import com.example.sistemabackenddebancos.iam.infrastructure.persistence.jpa.entities.MfaMethodEntity;
import com.example.sistemabackenddebancos.iam.infrastructure.persistence.jpa.entities.UserEntity;

import java.util.ArrayList;

public class UserPersistenceMapper {

    public static User toDomain(UserEntity e) {
        var methods = e.getMfaMethods().stream().map(me ->
                new MfaMethod(
                        new MfaMethodId(me.getId()),
                        me.getType(),
                        me.getDestination(),
                        me.isVerified(),
                        me.getSecret()
                )
        ).toList();

        return new User(
                new UserId(e.getId()),
                new Email(e.getEmail()),
                new PasswordHash(e.getPasswordHash()),
                e.getStatus(),
                e.isMfaEnabled(),
                methods,
                e.getFailedAttempts()
        );
    }

    public static UserEntity toEntity(User u) {
        var e = new UserEntity();
        e.setId(u.id().value());
        e.setEmail(u.email().value());
        e.setPasswordHash(u.passwordHash().value());
        e.setStatus(u.status());
        e.setMfaEnabled(u.mfaEnabled());
        e.setFailedAttempts(u.failedAttempts());

        var list = new ArrayList<MfaMethodEntity>();
        for (var m : u.mfaMethods()) {
            var me = new MfaMethodEntity();
            me.setId(m.id().value());
            me.setType(m.type());
            me.setDestination(m.destination());
            me.setVerified(m.verified());
            me.setSecret(m.secret());  // ← ¡ESTO ES LO QUE FALTA!
            me.setUser(e);
            list.add(me);
        }
        e.setMfaMethods(list);

        return e;
    }
}