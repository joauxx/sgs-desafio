package com.application.sgs.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "solicitante")
public class Solicitante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String nome;

    @Column(name = "cpf_cnpj", nullable = false, length = 20, unique = true)
    private String cpfCnpj;

    @Column(length = 255)
    private String email;

    @Column(length = 20)
    private String telefone;
}
