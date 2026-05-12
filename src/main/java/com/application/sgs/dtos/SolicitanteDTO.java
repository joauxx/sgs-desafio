package com.application.sgs.dtos;

public record SolicitanteDTO(
    Long id,
    String nome,
    String cpfCnpj,
    String email,
    String telefone
) {}
