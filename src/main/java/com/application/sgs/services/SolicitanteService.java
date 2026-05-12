package com.application.sgs.services;

import com.application.sgs.models.Solicitante;
import com.application.sgs.repositories.SolicitanteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SolicitanteService {

    private final SolicitanteRepository solicitanteRepository;

    public List<Solicitante> listarTodos() {
        return solicitanteRepository.findAll();
    }
}
