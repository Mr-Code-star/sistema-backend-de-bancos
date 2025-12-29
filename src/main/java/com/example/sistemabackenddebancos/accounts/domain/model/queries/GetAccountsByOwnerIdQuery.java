package com.example.sistemabackenddebancos.accounts.domain.model.queries;

import com.example.sistemabackenddebancos.accounts.domain.model.valueobjects.OwnerId;

public record GetAccountsByOwnerIdQuery(OwnerId ownerId) {}
