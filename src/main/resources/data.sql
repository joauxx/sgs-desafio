INSERT INTO categoria (nome, descricao) VALUES
('Material de Escritório', 'Compra de suprimentos como papel, canetas e pastas.'),
('Equipamentos de TI', 'Aquisição de computadores, monitores, teclados e periféricos.'),
('Manutenção Predial', 'Serviços de reparos elétricos, hidráulicos e ar-condicionado.'),
('Serviços Terceirizados', 'Pagamentos para consultorias,limpeza e segurança corporativa.'),
('Viagens e Deslocamentos', 'Pagamento de passagens, hospedagem e alimentação.');

INSERT INTO solicitante (nome, cpf_cnpj, email, telefone) VALUES
('João Carlos Silva', '123.456.789-01', 'joao.silva@exemplo.com', '(11) 98765-4321'),
('Maria Rita Gomes', '987.654.321-09', 'maria.gomes@exemplo.com', '(21) 91234-5678'),
('Tech Solutions Brasil Ltda', '12.345.678/0001-90', 'contato@techsolutions.com.br', '(11) 3000-4000'),
('Pedro Henrique Santos', '456.123.789-12', 'pedro.santos@exemplo.com', '(31) 99999-8888'),
('Comercial Varejista S.A.', '98.765.432/0001-10', 'financeiro@comercialvarejista.com.br', '(41) 3333-2222');

INSERT INTO solicitacao (solicitante_id, categoria_id, descricao, valor, status) VALUES
(1, 1, 'Caixas de papel sulfite A4 e toners para impressora.', 850.50, 'SOLICITADO'),
(2, 5, 'Passagens aéreas e hotel para conferência em São Paulo.', 3450.00, 'LIBERADO'),
(3, 4, 'Pagamento mensal do serviço de consultoria em segurança da informação.', 12500.00, 'APROVADO'),
(4, 2, 'Dois monitores de 27 polegadas para a equipe de design.', 2980.90, 'REJEITADO'),
(5, 3, 'Reparo no sistema de refrigeração da sala de servidores.', 1800.00, 'CANCELADO');
