# Gestão de Pensões

Aplicação desktop Java para registo e acompanhamento de beneficiários,
pensionistas, pensões e pagamentos. A interface Swing está ligada aos serviços
de domínio e à persistência Hibernate/H2.

## Requisitos e execução

- JDK 25
- Maven 3.9 ou superior (ou o Maven Wrapper incluído)

Na pasta `Gestao_de_Pensoes`, execute:

```text
.\mvnw.cmd test
.\mvnw.cmd exec:java
```

Na primeira execução, a aplicação cria os perfis predefinidos e pede os dados
para criar a conta de administrador inicial. Não existe uma senha inicial
partilhada ou predefinida. Nas execuções seguintes, é obrigatório iniciar sessão
com uma conta ativa. O menu permite terminar a sessão e autenticar outro
utilizador.

Por omissão, o Hibernate guarda os dados em H2 no ficheiro
`%USERPROFILE%\.gestao-pensoes\dados` no Windows (ou
`~/.gestao-pensoes/dados` noutros sistemas). Para usar outra base H2, defina a
propriedade de sistema ou variável de ambiente `GESTAO_PENSOES_DB_URL` antes de
iniciar.

## Navegação e funcionalidades

- **Painel principal:** contagem de beneficiários, pensionistas e pensões,
  além do total mensal das pensões ativas.
- **Beneficiários:** criar, listar, pesquisar, ordenar, editar e eliminar.
- **Pensionistas:** criar, listar, pesquisar, ordenar, editar e eliminar.
- **Pensões e pagamentos:** registar e atualizar pensões, pesquisar por texto,
  filtrar por tipo e estado, ordenar colunas, executar ações do ciclo de vida e
  registar pagamentos mensais.
- **Relatórios:** exportar um resumo CSV de beneficiários e pensões e consultar
  o histórico persistido de relatórios.
- **Utilizadores:** disponível apenas para administradores; permite criar
  utilizadores com um dos três perfis e ativar/desativar contas.

As tabelas suportam ordenação pelas colunas. Os formulários apresentam erros de
validação ou falhas de persistência sem ocultar a causa; os erros inesperados
também são registados no logger da aplicação.

## Perfis e controlo de acesso

| Perfil | Operações permitidas |
| --- | --- |
| Administrador | Consulta, criação, edição e remoção de dados; relatórios e gestão de utilizadores |
| Gestor | Consulta, criação, edição e remoção de dados operacionais; relatórios |
| Operador | Consulta, criação, edição, pagamentos e relatórios; sem remoção ou gestão de utilizadores |

O utilizador autenticado é verificado ao construir a interface; opções não
autorizadas não são apresentadas ou ficam desativadas. A criação do primeiro
administrador não depende de credenciais de demonstração. As senhas novas são
guardadas com PBKDF2-HMAC-SHA256 e os hashes SHA-256 legados são atualizados
automaticamente depois de uma autenticação válida.

## Organização

- `model`: entidades e regras do domínio.
- `dao` e `repository`: persistência Hibernate e contratos CRUD.
- `service`: validações, autenticação, autorização e operações de negócio.
- `controller`: ligação das ações da interface aos serviços.
- `View`: arranque autenticado, navegação e interface Swing.
- `persistence`: configuração e ciclo de vida do Hibernate.

As pensões suportam as modalidades velhice, invalidez, sobrevivência e reduzida.
O estado inicial é pendente; transições posteriores seguem as regras do serviço
de pensões. O valor mensal é o valor introduzido no registo — não são aplicadas
fórmulas legais sem os parâmetros contributivos necessários.
