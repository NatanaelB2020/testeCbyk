# Desafio Conta API

Esta é uma API RESTful desenvolvida em Spring Boot para gerenciar contas financeiras. A API permite a criação, atualização, busca e importação de contas a partir de arquivos CSV, além de fornecer funcionalidades para calcular totais e filtrar contas com base em critérios específicos.

## Estrutura do Projeto

O projeto é dividido em duas partes principais: **Serviço (Service)** e **Controlador (Controller)**.

### Serviço (ContaService)

O `ContaService` é responsável pela lógica de negócios relacionada às contas. Abaixo estão os principais métodos implementados:

- **`createConta(Conta conta)`**: Cria uma nova conta e retorna a conta criada.
  
- **`createContas(List<Conta> contas)`**: Cria várias contas e retorna a lista de contas criadas.

- **`atualizarConta(Long id, Conta contaAtualizada)`**: Atualiza uma conta existente com base no ID fornecido. Se a conta não existir, retorna `null`.

- **`alterarSituacao(Long id, SituacaoConta novaSituacao)`**: Altera a situação de uma conta (por exemplo, PAGA ou PENDENTE).

- **`listarContas(Pageable pageable)`**: Lista todas as contas com paginação.

- **`buscarPorId(Long id)`**: Busca uma conta pelo ID e retorna uma `Optional<Conta>`.

- **`buscarComFiltro(LocalDate dataInicio, LocalDate dataFim, String descricao, int page, int size)`**: Busca contas com base em filtros como data de vencimento e descrição, retornando uma lista paginada.

- **`obterValorTotalPago(LocalDate start, LocalDate end)`**: Obtém o valor total pago em um intervalo de datas.

- **`importarContasFromCSV(MultipartFile file)`**: Importa contas a partir de um arquivo CSV e salva no repositório.

- **`validarConta(Conta conta)`**: Valida as informações da conta, garantindo que o valor seja positivo e a data de pagamento não seja anterior à data de vencimento.

### Controlador (ContaController)

O `ContaController` é responsável por expor os endpoints da API. Aqui estão os principais endpoints implementados:

- **`GET /api/contas/teste`**: Verifica se a aplicação está rodando.

- **`POST /api/contas/create`**: Cria uma nova conta. 
  - **Request Body**: `Conta`
  - **Response**: `Conta` criada.

- **`POST /api/contas/create/contas`**: Cria várias contas.
  - **Request Body**: `List<Conta>`
  - **Response**: `List<Conta>` com as contas criadas.

- **`PUT /api/contas/atualizar/{id}`**: Atualiza uma conta existente.
  - **Path Variable**: `id` da conta.
  - **Request Body**: `Conta`
  - **Response**: `Conta` atualizada ou `404 Not Found` se não existir.

- **`PATCH /api/contas/{id}/situacao`**: Altera a situação de uma conta.
  - **Path Variable**: `id` da conta.
  - **Request Param**: `SituacaoConta`
  - **Response**: `204 No Content`.

- **`GET /api/contas/lista`**: Lista as contas com paginação.
  - **Response**: `Page<Conta>`.

- **`GET /api/contas/buscar/{id}`**: Busca uma conta pelo ID.
  - **Path Variable**: `id`.
  - **Response**: `Conta` ou `404 Not Found`.

- **`GET /api/contas/filtro`**: Busca contas com base em filtros de data e descrição.
  - **Request Params**: `dataInicio`, `dataFim`, `descricao`, `page`, `size`.
  - **Response**: `Page<Conta>`.

- **`GET /api/contas/total-pago`**: Obtém o valor total pago em um intervalo de datas.
  - **Request Params**: `start`, `end`.
  - **Response**: `BigDecimal` com o valor total.

- **`POST /api/contas/importar`**: Importa contas de um arquivo CSV.
  - **Request Param**: `file` (MultipartFile).
  - **Response**: `204 No Content` ou `400 Bad Request` se o arquivo estiver vazio.

## Dependências

- Spring Boot
- Spring Data JPA
- Spring Web
- Hibernate
- Lombok

## Execução do Projeto

1. Clone o repositório:
   ```bash
   git clone <url-do-repositorio>
   cd <diretorio-do-projeto>
