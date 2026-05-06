DROP TABLE IF EXISTS solicitacao;
DROP TABLE IF EXISTS solicitante;
DROP TABLE IF EXISTS categoria;

CREATE TABLE categoria (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    descricao TEXT
);

CREATE TABLE solicitante (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    cpf_cnpj VARCHAR(20) NOT NULL,
    email VARCHAR(255),
    telefone VARCHAR(20),
    CONSTRAINT uk_solicitante_cpf_cnpj UNIQUE (cpf_cnpj)
);

CREATE TABLE solicitacao (
    id SERIAL PRIMARY KEY,
    solicitante_id INTEGER NOT NULL,
    categoria_id INTEGER NOT NULL,
    descricao TEXT NOT NULL,
    valor NUMERIC(15, 2) NOT NULL,
    data_solicitacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) DEFAULT 'SOLICITADO' NOT NULL,
    CONSTRAINT fk_solicitacao_solicitante FOREIGN KEY (solicitante_id) REFERENCES solicitante (id),
    CONSTRAINT fk_solicitacao_categoria FOREIGN KEY (categoria_id) REFERENCES categoria (id),
    CONSTRAINT ck_solicitacao_status CHECK (status IN ('SOLICITADO', 'LIBERADO', 'APROVADO', 'REJEITADO', 'CANCELADO'))
);
