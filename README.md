Bem-vindo ao repositório do backend do sistema SGS. Este projeto foi desenvolvido como parte de um desafio técnico e utiliza Java 17 com Spring Boot.

## Tecnologias Utilizadas

*   **Java 17**
*   **Spring Boot 3.x**
*   **Spring Web** (API REST)
*   **Spring Data JPA** (Persistência de dados)
*   **PostgreSQL** (Banco de dados relacional)
*   **Docker & Docker Compose** (Ambiente de desenvolvimento)

## Pré-requisitos

Para rodar este projeto localmente, você precisará ter instalado em sua máquina:
*   [Java 17 (JDK)](https://adoptium.net/)
*   [Docker](https://www.docker.com/) e Docker Compose (para subir o banco de dados)

## Como rodar o projeto localmente

**1. Suba o banco de dados via Docker:**
Na raiz do projeto, execute o seguinte comando para iniciar o container do PostgreSQL em segundo plano:
```bash
docker compose up -d
```
> O banco de dados estará disponível na porta `5432` local com o usuário `sgs_user`, senha `sgs_pass` e database `sgs_db`.

**2. Execute a aplicação Spring Boot:**
Com o banco rodando, inicie o projeto utilizando o Maven Wrapper que já vem embutido:
No Linux/macOS:
```bash
./mvnw spring-boot:run
```
No Windows:
```cmd
mvnw.cmd spring-boot:run
```

A API estará disponível em: `http://localhost:8080`.

## Como rodar os testes

Para executar a suíte de testes da aplicação, rode o comando:
No Linux/macOS:
```bash
./mvnw test
```
No Windows:
```cmd
mvnw.cmd test
```

## Derrubando o ambiente
Para parar e remover os containers do Docker, execute:
```bash
docker compose down
```

---
*Desenvolvido para o desafio técnico da vaga.*
