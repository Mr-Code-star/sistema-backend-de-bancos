package com.example.sistemabackenddebancos.shared.infrastructure.persistence.jpa.configuration.strategy;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategySnakeCaseImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

public class SnakeCaseWithPluralizedTablePhysicalNamingStrategy
        extends PhysicalNamingStrategySnakeCaseImpl {

    // ✅ constructor público vacío (importantísimo)
    public SnakeCaseWithPluralizedTablePhysicalNamingStrategy() {
        super();
    }

    @Override
    public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment context) {
        Identifier snake = super.toPhysicalTableName(name, context);
        if (snake == null) return null;

        String table = snake.getText();
        if (!table.endsWith("s")) table = table + "s";

        return Identifier.toIdentifier(table);
    }
}