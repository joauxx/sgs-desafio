package com.application.sgs.controllers;

import com.application.sgs.dtos.SolicitacaoCreateDTO;
import com.application.sgs.dtos.SolicitacaoResponseDTO;
import com.application.sgs.dtos.SolicitacaoStatusUpdateDTO;
import com.application.sgs.exceptions.EntityNotFoundException;
import com.application.sgs.models.Categoria;
import com.application.sgs.models.Solicitacao;
import com.application.sgs.models.Solicitante;
import com.application.sgs.repositories.CategoriaRepository;
import com.application.sgs.dtos.projections.SolicitacaoListagemProjection;
import com.application.sgs.repositories.SolicitanteRepository;
import com.application.sgs.services.SolicitacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/solicitacoes")
@RequiredArgsConstructor
public class SolicitacaoController {

    private final SolicitacaoService solicitacaoService;
    private final SolicitanteRepository solicitanteRepository;
    private final CategoriaRepository categoriaRepository;

    @PostMapping
    public ResponseEntity<SolicitacaoResponseDTO> criar(@Valid @RequestBody SolicitacaoCreateDTO dto) {
        Solicitacao solicitacao = converterParaEntidade(dto);
        Solicitacao solicitacaoSalva = solicitacaoService.criar(solicitacao);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(solicitacaoSalva.getId())
                .toUri();

        return ResponseEntity.created(uri).body(converterParaDTO(solicitacaoSalva));
    }

    @GetMapping
    public ResponseEntity<Page<SolicitacaoListagemProjection>> listar(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) LocalDateTime dataInicio,
            @RequestParam(required = false) LocalDateTime dataFim,
            Pageable pageable) {

        Page<SolicitacaoListagemProjection> solicitacoes = solicitacaoService.listarComFiltros(
                status, categoriaId, dataInicio, dataFim, pageable);

        return ResponseEntity.ok(solicitacoes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SolicitacaoResponseDTO> buscarPorId(@PathVariable Long id) {
        Solicitacao solicitacao = solicitacaoService.buscarPorId(id);
        return ResponseEntity.ok(converterParaDTO(solicitacao));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> alterarStatus(
            @PathVariable Long id,
            @Valid @RequestBody SolicitacaoStatusUpdateDTO dto) {

        solicitacaoService.alterarStatus(id, dto.status());
        return ResponseEntity.noContent().build();
    }

    private Solicitacao converterParaEntidade(SolicitacaoCreateDTO dto) {
        Solicitante solicitante = solicitanteRepository.findById(dto.solicitanteId())
                .orElseThrow(
                        () -> new EntityNotFoundException("Solicitante não encontrado com ID: " + dto.solicitanteId()));

        Categoria categoria = categoriaRepository.findById(dto.categoriaId())
                .orElseThrow(
                        () -> new EntityNotFoundException("Categoria não encontrada com ID: " + dto.categoriaId()));

        Solicitacao solicitacao = new Solicitacao();
        solicitacao.setSolicitante(solicitante);
        solicitacao.setCategoria(categoria);
        solicitacao.setDescricao(dto.descricao());
        solicitacao.setValor(dto.valor());

        // Data e Status já têm valores padrão na Entidade
        return solicitacao;
    }

    private SolicitacaoResponseDTO converterParaDTO(Solicitacao solicitacao) {
        return new SolicitacaoResponseDTO(
                solicitacao.getId(),
                solicitacao.getSolicitante().getNome(),
                solicitacao.getSolicitante().getCpfCnpj(),
                solicitacao.getCategoria().getNome(),
                solicitacao.getDescricao(),
                solicitacao.getValor(),
                solicitacao.getDataSolicitacao(),
                solicitacao.getStatus());
    }
}
