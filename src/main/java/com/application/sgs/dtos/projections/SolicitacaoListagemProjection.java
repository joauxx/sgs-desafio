package com.application.sgs.repositories;

import java.math.BigDecimal;

public interface SolicitacaoListagemProjection {
    String getNomeSolicitante();
    String getDocumentoSolicitante();
    String getNomeCategoria();
    String getStatus();
    BigDecimal getValor();
}
