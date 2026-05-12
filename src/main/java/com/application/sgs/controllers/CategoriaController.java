package com.application.sgs.controllers;

import com.application.sgs.dtos.CategoriaDTO;
import com.application.sgs.services.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    @GetMapping
    public ResponseEntity<List<CategoriaDTO>> listarTodas() {
        List<CategoriaDTO> categorias = categoriaService.listarTodas().stream()
                .map(categoria -> new CategoriaDTO(categoria.getId(), categoria.getNome(), categoria.getDescricao()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(categorias);
    }
}
