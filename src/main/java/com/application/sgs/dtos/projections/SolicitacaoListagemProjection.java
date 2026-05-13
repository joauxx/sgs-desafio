package com.application.sgs.dtos.projections;

import java.math.BigDecimal;

public interface SolicitacaoListagemProjection {
    Long getId();

    String getNomeSolicitante();

    String getDocumentoSolicitante();

    String getNomeCategoria();

    String getStatus();

    BigDecimal getValor();
}
