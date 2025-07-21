# ProntoSus - Gestão de Prontuário Eletrônico

## Descrição
Sistema para gestão de prontuários eletrônicos voltado para clínicas e hospitais, permitindo o cadastro, consulta e atualização de pacientes, agendamentos, registros médicos e arquivos relacionados.

## Cobertura de Testes
- Cobertura atual: **80%** dos testes automatizados.
- Meta próxima entrega: **95%** de cobertura.
- Os testes estão localizados em `src/test/java` e relatórios em `target/site/jacoco`.

## Arquitetura
- **Camadas:**
  - Adapters (Controllers REST)
  - Application (Casos de uso)
  - Domain (Entidades e regras de negócio)
  - Gateway (Persistência)
- **Padrão:** Arquitetura Limpa
- **Principais fluxos:**
  - Autenticação de usuários
  - Cadastro e consulta de pacientes
  - Agendamento de consultas
  - Registro e consulta de prontuários médicos
  - Upload e download de arquivos

## Tecnologias Utilizadas
- **Java 21**
- **Spring Boot**
- **Maven**
- **JUnit** e **Mockito** (testes)
- **JaCoCo** (relatórios de cobertura)
- **Docker** e **Docker Compose**
- **Banco de dados:** PostgreSQL

## Como rodar o projeto
1. **Pré-requisitos:**
   - Java 21
   - Maven
   - Docker
2. **Subir banco de dados:**
   - Execute: `docker-compose up -d`
3. **Rodar aplicação:**
   - `./mvnw spring-boot:run` (Linux/Mac)
   - `mvnw.cmd spring-boot:run` (Windows)
4. **Rodar testes:**
   - `./mvnw test`
5. **Acessar aplicação:**
   - Por padrão, disponível em `http://localhost:8000`

## Estrutura de Pastas
- `src/main/java`: Código principal
- `src/test/java`: Testes automatizados
- `src/main/resources`: Configurações e scripts
- `uploads/`: Arquivos enviados

## Contribuição
- Siga o padrão de arquitetura limpa
- Escreva testes para novas funcionalidades
- Consulte o arquivo `HELP.md` para dúvidas comuns

## Contato
Para dúvidas ou sugestões, abra uma issue ou envie e-mail para: `jessica.persou@gmail.com`
