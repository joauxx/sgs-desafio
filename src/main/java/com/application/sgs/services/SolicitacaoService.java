package com.application.sgs.services;

import com.application.sgs.models.Solicitacao;
import com.application.sgs.models.enums.StatusSolicitacao;
import com.application.sgs.exceptions.StatusException;
import com.application.sgs.repositories.SolicitacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SolicitacaoService {

    private final SolicitacaoRepository solicitacaoRepository;

    @Transactional
    public Solicitacao criar(Solicitacao solicitacao) {
        return solicitacaoRepository.save(solicitacao);
    }

    public Solicitacao buscarPorId(Long id) {
        return solicitacaoRepository.findById(id)
                .orElseThrow(() -> new com.application.sgs.exceptions.EntityNotFoundException("Solicitação não encontrada para o ID: " + id));
    }

    public org.springframework.data.domain.Page<com.application.sgs.dtos.projections.SolicitacaoListagemProjection> listarComFiltros(
            String status, Long categoriaId, java.time.LocalDateTime dataInicio, java.time.LocalDateTime dataFim, org.springframework.data.domain.Pageable pageable) {
        return solicitacaoRepository.buscarSolicitacoesComFiltros(status, categoriaId, dataInicio, dataFim, pageable);
    }


    @Transactional
    public Solicitacao alterarStatus(Long id, StatusSolicitacao novoStatus) {
        Solicitacao solicitacao = solicitacaoRepository.findById(id)
                .orElseThrow(() -> new com.application.sgs.exceptions.EntityNotFoundException("Solicitação não encontrada para o ID: " + id));

        StatusSolicitacao statusAtual = solicitacao.getStatus();

        if (statusAtual == novoStatus) {
            throw new StatusException("A solicitação já se encontra no status " + novoStatus);
        }

        boolean transicaoPermitida = false;

        switch (statusAtual) {
            case SOLICITADO:
                if (novoStatus == StatusSolicitacao.LIBERADO || novoStatus == StatusSolicitacao.REJEITADO) {
                    transicaoPermitida = true;
                }
                break;
            case LIBERADO:
                if (novoStatus == StatusSolicitacao.APROVADO || novoStatus == StatusSolicitacao.REJEITADO) {
                    transicaoPermitida = true;
                }
                break;
            case APROVADO:
                if (novoStatus == StatusSolicitacao.CANCELADO) {
                    transicaoPermitida = true;
                }
                break;
            case REJEITADO:
            case CANCELADO:
                throw new StatusException("Estados REJEITADO e CANCELADO são finais e não podem ser alterados.");
        }

        if (!transicaoPermitida) {
            throw new StatusException(
                    "Transição inválida de status: de " + statusAtual + " para " + novoStatus);
        }

        solicitacao.setStatus(novoStatus);
        return solicitacaoRepository.save(solicitacao);
    }
}
