package com.example.sistemabackenddebancos.transfers.domain.model.queries;

import java.util.UUID;

public record GetTransfersByAccountIdQuery(UUID accountId) {}