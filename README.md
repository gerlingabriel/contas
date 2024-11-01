# Desafio Backend - Sistema de Contas a Pagar

Este projeto implementa uma API REST para gerenciar um sistema de contas a pagar, possibilitando o CRUD de contas, atualização de status de pagamento, visualização e importação de contas via arquivo CSV. A aplicação é implementada em Java 17 com Spring Boot, utilizando PostgreSQL como banco de dados e orquestrada em contêineres Docker com Docker Compose.

---

Estrutura do Projeto
Aqui está a estrutura do projeto:

```C:.
ª   .gitattributes
ª   .gitignore
ª   docker-compose.yml
ª   Dockerfile
ª   estrutura.txt
ª   HELP.md
ª   mvnw
ª   mvnw.cmd
ª   pom.xml
ª   
+---.mvn
ª   +---wrapper
ª           maven-wrapper.properties
ª           
+---.vscode
ª       launch.json
ª       settings.json
ª       
+---src
ª   +---main
ª   ª   +---java
ª   ª   ª   +---com
ª   ª   ª       +---teste
ª   ª   ª           +---contas
ª   ª   ª               ª   ContasApplication.java
ª   ª   ª               ª   
ª   ª   ª               +---application
ª   ª   ª               ª   +---controller
ª   ª   ª               ª   ª       ContaController.java
ª   ª   ª               ª   ª       
ª   ª   ª               ª   +---dto
ª   ª   ª               ª           ContaAtualizadaRequest.java
ª   ª   ª               ª           ContaNovaRequest.java
ª   ª   ª               ª           ContaPagaRequest.java
ª   ª   ª               ª           ListaContaPagaResponse.java
ª   ª   ª               ª           
ª   ª   ª               +---domain
ª   ª   ª               ª   +---exception
ª   ª   ª               ª   ª       ContaInvalidException.java
ª   ª   ª               ª   ª       ContaNotFoundException.java
ª   ª   ª               ª   ª       GlobalExceptionHandler.java
ª   ª   ª               ª   ª       
ª   ª   ª               ª   +---model
ª   ª   ª               ª   ª       Conta.java
ª   ª   ª               ª   ª       Situacao.java
ª   ª   ª               ª   ª       
ª   ª   ª               ª   +---repository
ª   ª   ª               ª   ª       ContaRepository.java
ª   ª   ª               ª   ª       
ª   ª   ª               ª   +---service
ª   ª   ª               ª           ContaService.java
ª   ª   ª               ª           
ª   ª   ª               +---infrastructure
ª   ª   ª                       SecurityConfig.java
ª   ª   ª                       
ª   ª   +---resources
ª   ª       ª   application.properties
ª   ª       ª   application.yml
ª   ª       ª   
ª   ª       +---db
ª   ª       ª   +---migration
ª   ª       ª           V1_create_conta.sql
ª   ª       ª           
ª   ª       +---static
ª   ª       +---templates
ª   +---test
ª       +---java
ª           +---com
ª               +---teste
ª                   +---contas
ª                       ª   ContasApplicationTests.java
ª                       ª   
ª                       +---domain
ª                           +---service
ª                                   ContaServiceTest.java
```

# Tecnologias e Ferramentas Utilizadas
* Java 17: Linguagem de programação principal.
* Spring Boot: Framework para construção de aplicações Java.
* PostgreSQL: Banco de dados utilizado.
* Docker e Docker Compose: Para orquestração de contêineres e execução do projeto.
* Flyway: Controle de versões de banco de dados.
* JPA (Java Persistence API): Para mapeamento objeto-relacional.
* DDD (Domain Driven Design): Arquitetura orientada ao domínio.
* Autenticação Simples: Implementação de autenticação básica.

# Endpoints Implementados
* Cadastrar Conta
* Atualizar Conta
* Alterar Situação da Conta
* Obter Lista de Contas a Pagar - Filtrada por data de vencimento e descrição
* Obter Conta por ID
* Obter Valor Total Pago por Período
* Importar Lote de Contas - A partir de arquivo CSV

## Estrutura da Tabela "Conta"
O banco de dados possui uma tabela para armazenar as contas a pagar, com os seguintes campos:

- id: Identificador único da conta
- data_vencimento: Data de vencimento da conta
- data_pagamento: Data de pagamento da conta (quando aplicável)
- valor: Valor da conta
- descricao: Descrição da conta
- situacao: Situação da conta (por exemplo, PENDENTE, PAGO)
- Requisitos de Execução
- Docker e Docker Compose instalados.
- Conta de banco de dados configurada para PostgreSQL.


# Passo a Passo de Execução
Clone o Repositório
bash
Copiar código
git clone https://github.com/seu-usuario/seu-repositorio.git
cd seu-repositorio
Configure as Variáveis de Ambiente

Atualize as variáveis necessárias para o banco de dados no arquivo .env, incluindo usuário, senha e nome do banco.

Construa e Inicie os Contêineres

Execute o seguinte comando para iniciar a aplicação e o banco de dados em contêineres Docker:
bash
```
docker-compose up
```
Este comando:

Cria e inicializa o banco de dados PostgreSQL.
Executa a aplicação Java.
Realiza as migrações de banco de dados automaticamente com o Flyway.

## Autenticação

Autenticação básica (username: teste, password: teste123@).
Executando as APIs
Use ferramentas como Postman ou cURL para realizar chamadas para a API. Certifique-se de adicionar a autenticação básica em cada requisição.

## Requisições
Para facilitar o uso e testes da API, você pode utilizar a coleção do Postman com as requisições predefinidas. Esta coleção inclui todos os endpoints disponíveis e permite realizar operações como criação, atualização, busca, e importação de contas.
1. Cadastrar Conta
```
curl --location 'http://localhost:8888/totvs/api/conta/v1.0' \
--header 'Content-Type: application/json' \
--header 'Authorization: Basic dGVzdGU6dGVzdGUxMjNA' \
--data '{
  "dataVencimento" : "2024-11-05",
  "valor": 55,
  "descricao" : "Celular Vivo"
}'
````

2. Atualizar Situação da Conta
```
curl --location --request PUT 'http://localhost:8888/totvs/api/conta/v1.0' \
--header 'Content-Type: application/json' \
--header 'Authorization: Basic dGVzdGU6dGVzdGUxMjNA' \
--data '{
  "id": 1,
  "dataVencimento" : "2024-11-20",
  "valor": 120,
  "descricao" : "Academia"
}'
```
3. Importar Contas via CSV
```
curl --location 'http://localhost:8888/totvs/api/conta/v1.0/importar' \
--header 'Authorization: Basic dGVzdGU6dGVzdGUxMjNA' \
--form 'file=@"/C:/Users/Gabriel/Desktop/contas.csv"'
```
4. Buscar Contas Pagas
```
curl --location --request GET 'http://localhost:8888/totvs/api/conta/v1.0/contaspagas?page=0&size=10' \
--header 'Content-Type: application/json' \
--header 'Authorization: Basic dGVzdGU6dGVzdGUxMjNA' \
--data '{
  "dataVencimento" : "2024-11-20",
  "descricao" : "Academia"
}'
```
5. Alterar Situacao
```
curl --location --request PUT 'http://localhost:8888/totvs/api/conta/v1.0/2/alterarsituacao?situacao=ATRASADO' \
--header 'Authorization: Basic dGVzdGU6dGVzdGUxMjNA'
```
6. Buscar Conta
```
curl --location 'http://localhost:8888/totvs/api/conta/v1.0/2' \
--header 'Authorization: Basic dGVzdGU6dGVzdGUxMjNA'
```
7. Somar Contas Pagas
```
curl --location 'http://localhost:8888/totvs/api/conta/v1.0/periodopago?dataInicio=2024-10-01&dataFim=2024-12-01' \
--header 'Authorization: Basic dGVzdGU6dGVzdGUxMjNA'
``` 

## Exemplo de Importação de Contas via CSV
Abaixo está um exemplo de como o arquivo CSV deve ser formatado para a importação de contas. Certifique-se de que o arquivo siga essa estrutura ao usar o endpoint /importar.

contas.csv  
``` 
"data_vencimento,data_pagamento,valor,descricao,situacao" 
"2024-11-01,2024-11-10,100.50,Conta de luz,PAGO"
"2024-11-05,,200.00,Conta de agua,PENDENTE"
```