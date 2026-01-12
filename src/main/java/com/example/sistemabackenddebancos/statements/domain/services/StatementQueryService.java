package com.example.sistemabackenddebancos.statements.domain.services;

import com.example.sistemabackenddebancos.statements.domain.model.aggregates.Statement;
import com.example.sistemabackenddebancos.statements.domain.model.queries.*;

public interface StatementQueryService {
    Statement handle(GetStatementByAccountQuery query);
    Statement handle(GetFullStatementByAccountQuery query);

}
