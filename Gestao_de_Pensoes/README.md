# Gestão de Pensões

Aplicação desktop Java para gestão de beneficiários, pensões e prestações.

## Requisitos e execução

- JDK 25
- Maven 3.9 ou superior

Na pasta `Gestao_de_Pensoes`, execute:

```text
mvn clean test
mvn exec:java
```

O Hibernate usa H2 em modo ficheiro e cria a base de dados em
`%USERPROFILE%\.gestao-pensoes\dados` no Windows (ou
`~/.gestao-pensoes/dados` noutros sistemas). Para apontar para outra base H2,
defina a variável de ambiente `GESTAO_PENSOES_DB_URL` antes de iniciar.

## Organização e conceitos

- `model`: entidades e regras do domínio. `Pessoa` e `Pensao` são classes
  abstratas; beneficiários e pensionistas herdam de `Pessoa`, enquanto as
  modalidades concretas herdam de `Pensao`.
- `repository`: contrato genérico `RepositorioCrud` e implementações em
  memória e Hibernate.
- `service`: validação, pesquisa e operações do domínio.
- `controller`: recebe os dados e eventos CRUD da janela Swing e coordena o
  serviço; a apresentação permanece em `View`.
- `persistence`: configuração e ciclo de vida do Hibernate.

As classes Java herdam implicitamente de `java.lang.Object`, a classe universal
da linguagem. O CRUD de beneficiários usa interface de repositório, Hibernate,
transações e uma base H2 persistente. A janela Swing usa componentes AWT e
eventos para criar, listar, pesquisar, atualizar e eliminar beneficiários.

As estruturas de coleção aparecem nas operações reais: `ArrayList` para
resultados, `Vector` para o registo sincronizado de operações e `Stack` para
permitir desfazer a última eliminação. `PesquisaRecursiva` demonstra pesquisa
binária recursiva numa lista ordenada; arrays são usados na validação dos
campos e na construção das linhas da tabela.
