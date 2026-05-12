package com.application.sgs.dtos;

import com.application.sgs.models.enums.StatusSolicitacao;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SolicitacaoResponseDTO(
    Long id,
    String nomeSolicitante,
    String documentoSolicitante,
    String nomeCategoria,
    String descricao,
    BigDecimal valor,
    LocalDateTime dataSolicitacao,
    StatusSolicitacao status
) {}
