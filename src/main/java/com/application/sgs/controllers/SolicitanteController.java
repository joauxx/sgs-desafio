package com.application.sgs.controllers;

import com.application.sgs.dtos.SolicitanteDTO;
import com.application.sgs.services.SolicitanteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/solicitantes")
@RequiredArgsConstructor
public class SolicitanteController {

    private final SolicitanteService solicitanteService;

    @GetMapping
    public ResponseEntity<List<SolicitanteDTO>> listarTodos() {
        List<SolicitanteDTO> solicitantes = solicitanteService.listarTodos().stream()
                .map(s -> new SolicitanteDTO(s.getId(), s.getNome(), s.getCpfCnpj(), s.getEmail(), s.getTelefone()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(solicitantes);
    }
}
