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

O arranque abre diretamente a interface Swing de gestão de beneficiários;
não é necessário navegar por um ecrã de início de sessão ainda sem
autenticação implementada.

O Hibernate usa H2 em modo ficheiro e cria a base de dados em
`%USERPROFILE%\.gestao-pensoes\dados` no Windows (ou
`~/.gestao-pensoes/dados` noutros sistemas). Para apontar para outra base H2,
defina a variável de ambiente `GESTAO_PENSOES_DB_URL` antes de iniciar.

## Organização e conceitos

- `model`: entidades e regras do domínio. `Pessoa` e `Pensao` são classes
  abstratas; beneficiários e pensionistas herdam de `Pessoa`, enquanto as
  modalidades concretas herdam de `Pensao`.
- `dao`: contratos e implementações Hibernate para persistir pensões e
  pagamentos; `repository` mantém o contrato CRUD genérico e a implementação
  em memória usada nos testes e noutros serviços.
- `service`: validação, pesquisa e operações do domínio.
- `controller`: recebe os dados e eventos CRUD da janela Swing e coordena o
  serviço; a apresentação permanece em `View`.
- `persistence`: configuração e ciclo de vida do Hibernate.

As classes Java herdam implicitamente de `java.lang.Object`, a classe universal
da linguagem. O CRUD de beneficiários usa interface de repositório, Hibernate,
transações e uma base H2 persistente. A janela Swing usa componentes AWT e
eventos para criar, listar, pesquisar, atualizar e eliminar beneficiários.

O módulo de pensões permite registar e atualizar pensões dos tipos velhice,
invalidez, sobrevivência e reduzida; pesquisar por tipo, estado ou pensionista;
aprovar, suspender, reativar, cancelar, arquivar e eliminar registos não ativos.
Também permite registar e consultar pagamentos mensais, com uma única liquidação
por pensão e mês, apenas para pensões ativas. O resumo apresenta a soma mensal
das pensões ativas. O valor mensal é informado no registo: não se aplicam
fórmulas legais de cálculo sem os parâmetros legais e contributivos definidos
para o caso. Na tabela de pensões, selecione um registo e abra o menu de contexto
para as operações do ciclo de vida, pagamentos e eliminação; `Ctrl+N` inicia um
novo registo.

As estruturas de coleção aparecem nas operações reais: `ArrayList` para
resultados, `Vector` para o registo sincronizado de operações e `Stack` para
permitir desfazer a última eliminação. `PesquisaRecursiva` demonstra pesquisa
binária recursiva numa lista ordenada; arrays são usados na validação dos
campos e na construção das linhas da tabela.
