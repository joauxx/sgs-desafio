package com.application.sgs.repositories;

import com.application.sgs.models.Solicitacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SolicitacaoRepository extends JpaRepository<Solicitacao, Long> {

    @Query(nativeQuery = true, value = """
            SELECT 
                sol.nome AS nomeSolicitante,
                sol.cpf_cnpj AS documentoSolicitante,
                cat.nome AS nomeCategoria,
                s.status AS status,
                s.valor AS valor
            FROM solicitacao s
            INNER JOIN solicitante sol ON s.solicitante_id = sol.id
            INNER JOIN categoria cat ON s.categoria_id = cat.id
            WHERE (CAST(:status AS VARCHAR) IS NULL OR s.status = CAST(:status AS VARCHAR))
              AND (CAST(:categoriaId AS BIGINT) IS NULL OR s.categoria_id = CAST(:categoriaId AS BIGINT))
              AND (CAST(:dataInicio AS TIMESTAMP) IS NULL OR s.data_solicitacao >= CAST(:dataInicio AS TIMESTAMP))
              AND (CAST(:dataFim AS TIMESTAMP) IS NULL OR s.data_solicitacao <= CAST(:dataFim AS TIMESTAMP))
            """)
    org.springframework.data.domain.Page<SolicitacaoListagemProjection> buscarSolicitacoesComFiltros(
            @Param("status") String status,
            @Param("categoriaId") Long categoriaId,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim,
            org.springframework.data.domain.Pageable pageable
    );
}
