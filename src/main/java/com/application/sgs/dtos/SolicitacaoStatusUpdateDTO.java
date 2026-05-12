package com.application.sgs.dtos;

import com.application.sgs.models.enums.StatusSolicitacao;
import jakarta.validation.constraints.NotNull;

public record SolicitacaoStatusUpdateDTO(
    @NotNull(message = "O novo status é obrigatório")
    StatusSolicitacao status
) {}
