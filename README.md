# SGS - Sistema de Gestão de Solicitações

**Um sistema corporativo para orquestração, controle financeiro e aprovação de requisições operacionais.**

O **SGS** foi arquitetado para resolver o problema de governança sobre requisições e gastos. Ele estabelece um fluxo de trabalho seguro, rastreável e rigoroso, garantindo que solicitações transitem por uma **Máquina de Estados** puramente controlada antes de seu deferimento ou cancelamento. 

---

## Visão Geral

Em ambientes corporativos, aprovações financeiras não podem trafegar de maneira desorganizada. O **SGS** resolve isso centralizando o fluxo: um solicitante abre uma requisição com valor e categoria e os gestores interagem evoluindo seu status (Liberação, Aprovação ou Rejeição) de forma restrita e rastreável.

O sistema foca ativamente em **alta performance de banco de dados** e **consistência de domínio**, garantindo integridade transacional e tratamento elegante de exceções ao longo da esteira.

---

## Principais Funcionalidades

- **Cadastro de Solicitações:** Criação de novas requisições atreladas aos limites de Solicitantes e Categorias pré-existentes.
- **Listagem Paginada Dinâmica:** Consulta estruturada com prevenção arquitetural a gargalos em larga escala.
- **Filtros Combinados Multi-parâmetros:** Busca de alta precisão por `Status`, `Categoria`, e `Intervalo de Datas`.
- **Fluxo de Atualização de Status:** Transição segura e auditável do ciclo de vida da requisição financeira.
- **Detalhamento de Entidade:** Visualização granular dos registros associados à abertura do chamado.
- **Máquina de Estados Estrita:** Proteção severa de regras de negócio em Backend contra transições ilegais de status.
- **Validações Baseadas em Contratos (DTOs):** Bloqueio de Payload mal formatado antes da entrada no banco.
- **Tratamento Global de Erros:** Interceptador REST devolvendo JSONs tipados e limpos para qualquer falha.

---

## Stack Tecnológica

- **Java 17:** Versão LTS madura.
- **Spring Boot 3.x:** Core framework escolhido por sua produtividade em injeção de dependências.
- **PostgreSQL:** Banco relacional robusto, com capacidade transacional e otimizador de queries avançado.
- **Native Query & Projections (Spring Data JPA):**  Escolha arquitetural para suplantar o gargalo de performance do Hibernate em listagens densas.
- **Docker / Docker Compose:** Isola as dependências de ambiente em contêineres e elimina quase toda a barreira de *onboarding* local.
- **Servidor Integrado (Static Content):** O client web usa HTML, JavaScript e Bootstrap, servidos nativamente pelo Tomcat do próprio Spring Boot.

---

## Diferenciais Técnicos

- **Máquina de Estados Isolada no Service:** Evitei o *anti-pattern* de "Fat Controller". As lógicas pesadas residem estritamente na camada de serviço, blindando o app e facilitando testes unitários e de integração.
- **SQL Nativo Tolerante a Filtros (Dinâmico):** Utilização do bloco `(CAST(:param AS VARCHAR) IS NULL OR coluna = CAST(:param AS VARCHAR))` direto em SQL, permitindo ao Postgres avaliar *short-circuits* otimizados sem poluir o backend com pesadas *CriteriaBuilders*.
- **DTOs como First-Class Citizens:** Nenhum dado trafega pelas fronteiras REST carregando atributos intrínsecos das entidades JPA, cortando o vetor de ataque de Mass Assignment.
- **OSIV (Open-In-View) Desabilitado:** Fixado na raiz (`application.properties`). Preservo o Hikari Connection Pool, provando que o ciclo de transação JPA/Hibernate se encerra no *Service*, jamais mantendo conexões pesadas abertas durante o fluxo TCP/Web de serialização Jackson.
- **Global Exception Handling Centralizado:** *Try-catches* dispersos foram extirpados. Toda anomalia cai na Controller Advice para virar um DTO (`ErrorResponseDTO`) elegante e imutável.

---


## Modelagem do Domínio e Relacionamentos

As operações possuem 3 grandes Entidades fortemente tipadas no banco:
1. **Solicitante:** Atores operacionais (com restrição de CPF/CNPJ único na base).
2. **Categoria:** Clusters orçamentários (ex: Operações, RH, TI).
3. **Solicitação:** Entidade transacional com duas **Foreign Keys**, valor de lastro, data contábil (timestamp) e um rastreio rigoroso de seu **Status** operacional.

---

## Máquina de Estados (State Machine)

A validade sistêmica depende inteiramente da proteção do ciclo de vida das transações. Esta máquina de regras foi implementada no Backend para nunca confiar no fluxo vindo do Web/Frontend.

**Fluxos Mapeados e Legalmente Permitidos:**
- 🟢 `SOLICITADO` ➔ `LIBERADO` *(Avanço para análise de nível 1)*
- 🟢 `LIBERADO` ➔ `APROVADO` *(Sanção e liquidação final do recurso)*
- 🔴 `SOLICITADO` ➔ `REJEITADO` *(Veto prévio no atendimento)*
- 🔴 `LIBERADO` ➔ `REJEITADO` *(Veto durante esteira de verificação)*
- 🔴 `APROVADO` ➔ `CANCELADO` *(Anulação extraordinária)*

**Estados Finais (Blindados e Imutáveis):**
As categorias `REJEITADO` ou `CANCELADO` atuam como estados finais imutáveis. Atingidos estes estados, a mutabilidade da entidade é fisicamente travada no Java, respondendo com status code HTTP `400 (Bad Request)` para violações de negócios impostas pela API.

---

## SQL Nativo vs Hibernate

Em um projeto de nível corporativo, delegar queries multi-paramétricas de JOIN massivo para a abstração HQL/JPA do Hibernate fatalmente geraria consultas sujas (n+1, cross joins ou table scans forçados). 

Para demonstrar domínio de modelagem e consultas relacionais, o método `buscarSolicitacoesComFiltros` implementou:
- Paginação injetada nativamente em um bloco construído à mão (`@Query(nativeQuery = true)`).
- Controle manual da subquery estrita em `countQuery`, economizando memória do servidor ao calcular os limites totais da paginação.
- Operadores booleanos baseados na arquitetura interna do PostgreSQL para resolver filtragem dinâmica na mesma requisição DQL.

---

## Scripts de Banco de Dados (DDL e DML)

Os scripts responsáveis pela estruturação e população inicial do banco de dados estão localizados no diretório de recursos da aplicação e são gerenciados automaticamente pelo Spring Boot:

```text
src/main/resources/
├── schema.sql
└── data.sql
```

#### **`schema.sql`**
Responsável pela criação completa da estrutura relacional da aplicação, garantindo a integridade desde o primeiro boot:
* **Estrutura:** Definição de tabelas, chaves primárias (PK) e chaves estrangeiras (FK).
* **Constraints:** Implementação de regras de integridade (como `UNIQUE` para CPF/CNPJ) e índices.
* **Relacionamentos:** Mapeamento físico das relações entre Solicitante, Categoria e Solicitação.

#### **`data.sql`**
Responsável pela carga inicial de dados (Seed), permitindo a execução e testes imediatos do sistema:
* **Categorias:** Cadastro de tipos de pagamento (Ex: Serviços, Material, Transporte).
* **Solicitantes:** População de usuários iniciais (mínimo de 5 registros conforme requisito).
* **Solicitações** Dados Referentes a solicitações iniciais (mínimo de 5 registros conforme requisito).

Ambos os arquivos são executados automaticamente durante a inicialização da aplicação pelo próprio Spring Boot, permitindo que o ambiente fique pronto para uso sem necessidade de intervenção manual no banco de dados.

---

## Como Executar o Projeto (Guia Rápido de Onboarding)

Entendo que o tempo do avaliador é precioso. Siga as instruções abaixo:

### Opção A: Execução Integrada (All-in-One)

1. **Suba os Containers (Banco de Dados):**
   Abra um terminal root e provisione o Postgres em background.
   ```bash
   docker compose up -d
   ```
   *(DB ativo porta: 5432).*

2. **Inicie o Container/Runtime do Spring Boot:**
   No diretório base do projeto, empacote e rode o artefato:
   ```bash
   ./mvnw spring-boot:run
   ```
   *(API subirá em: localhost:8080).*

3. **Acesse o Sistema:**
   Como o frontend web foi empacotado e está sendo servido diretamente pelo Spring Boot, basta abrir no seu navegador:
   🔗 **[http://localhost:8080](http://localhost:8080)**

### Opção B: Execução via IDE (Visual Studio Code / IntelliJ)

1. Certifique-se de ter os pacotes de expansão nativos instalados (Ex: *Extension Pack for Java*).
2. Dê `docker compose up -d` no terminal integrado.
3. Aperte o atalho ▶️ (Run) presente em cima do `public static void main` na classe base `SgsApplication.java`.
4. Abra **http://localhost:8080** no navegador.

---

---

## Endpoints de Integração (API Reference)

A aplicação foi planejada sob estritos conceitos de *Maturity Model REST Nível 2*:

| HTTP | URI Endpoint | Objetivo do Domínio |
| :--- | :--- | :--- |
| `GET` | `/solicitacoes` | Retorna Array paginado contendo Projection Views com filtros opcionais em QueryParams. |
| `POST` | `/solicitacoes` | Processa payload DTO e comita abertura em estado 0 (`SOLICITADO`). |
| `GET` | `/solicitacoes/{id}` | Resgata Payload enriquecido (Entidade cheia) atrelada ao Ticket ID. |
| `PATCH` | `/solicitacoes/{id}/status`| Valida Input e empurra a transação pela Máquina de Estados validada. |
| `GET` | `/categorias` | Lista as opções dimensionais de Categoria (Para Popular Selects). |
| `GET` | `/solicitantes` | Lista Atores do Sistema (Para Popular Selects de Abertura). |

---

## Exception Handler e Padrão de Contrato

Logs estourados com Java StackTraces e JSONs amorfos não existem nesta implementação.

Utilizei o `@RestControllerAdvice` acoplado ao Spring para estrangular de `NullPointers` até `EntityNotFoundException`. Tudo vira uma casca REST impecavelmente controlada baseada num Payload de Retorno Fixo (DTO de Error):

```json
{
  "timestamp": "2026-05-13T22:45:00.123",
  "status": 400,
  "error": "Bad Request",
  "message": "Transição inválida de status: de APROVADO para REJEITADO"
}
```

---

## Considerações Finais

Este projeto foi desenvolvido com foco em clareza arquitetural, previsibilidade de comportamento e aderência rigorosa aos requisitos propostos no desafio técnico.

Ao longo da implementação, priorizei:

* separação de responsabilidades;
* organização estrutural;
* consistência das regras de negócio;
* integridade do fluxo de estados;
* desempenho nas consultas SQL;
* simplicidade operacional;
* legibilidade e manutenção futura do código.

Todas as decisões técnicas foram tomadas buscando equilibrar simplicidade, robustez e facilidade de avaliação, respeitando o escopo solicitado e evitando complexidade desnecessária.

O objetivo principal foi construir uma aplicação coesa, segura e organizada, seguindo boas práticas amplamente utilizadas no desenvolvimento de sistemas corporativos.

