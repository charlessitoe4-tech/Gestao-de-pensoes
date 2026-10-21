# Sistema de Gestão de Pensões

Aplicação desktop Java Swing para gestão de beneficiários, processos de pensão, pagamentos e relatórios. Usa Maven, Hibernate/JPA e uma base de dados H2 em ficheiro (`~/.gestao-pensoes/dados`).

## Executar

Abra a pasta `Gestao_de_Pensoes` no NetBeans como projeto Maven (JDK 21 ou superior). Na linha de comandos:

```bash
mvn clean test
mvn exec:java
```

O ponto de entrada é o ecrã de login (`View.tela`).

## Contas predefinidas (3 perfis)

Sempre que o ecrã de login abre, o sistema garante que as contas abaixo existem, estão ativas e têm o perfil correto. Se uma delas existir com outra senha, a senha volta à predefinida. As senhas são guardadas como hash (SHA-256 com salt).

| Perfil | Utilizador | Senha |
|---|---|---|
| Administrador | `admin` | `Admin@123` |
| Gestor | `gestor` | `Gestor@123` |
| Operador | `operador` | `Operador@123` |

O botão **cadastrar** no ecrã de login pede a senha da conta `admin` antes de abrir o formulário de registo de novos utilizadores. O nome de utilizador é gerado a partir do email, as senhas são mascaradas e há verificação de duplicados.

## Controlo de acesso

| Perfil | Acesso |
|---|---|
| Administrador | Todos os menus, incluindo a gestão de utilizadores |
| Gestor | Beneficiários, processos, pagamentos, relatórios e documentos |
| Operador | Registo e consulta de beneficiários, pensões e documentos |

## Organização MVC

- `model/` e `model/enums/`: entidades de domínio e enumerações.
- `View/`: formulários Swing (NetBeans GUI Builder, formato Form + Java).
- `controller/`: recebem os eventos da interface e delegam às regras de negócio.
- `service/`: validações e regras de negócio.
- `dao/` e `repository/`: acesso e persistência dos dados.
- `persistence/`: configuração do Hibernate e criação das contas predefinidas.
- `Util/`: utilitários (hash de senhas).
- `src/test/java/`: testes automatizados (JUnit 5).

## Funcionalidades ligadas à interface

- Navegação entre ecrãs pelo menu lateral (CardLayout).
- Beneficiários: registar, editar, pesquisar e carregar os dados ao selecionar uma linha da tabela.
- Processos de pensão: pesquisar e ver os detalhes do processo selecionado.
- Documentos: seleção de ficheiros associados.
- Análise: os botões "aprovado" e "reprovado" mudam o estado do processo selecionado (Ativa ou Cancelada); só Administrador e Gestor.
- Documentos: "salvar" copia o ficheiro para `~/.gestao-pensoes/documentos` e regista-o na base de dados; "visualizar" abre o ficheiro.
- Pensões por morte e por aposentadoria: o botão Salvar regista o processo na base de dados.
- Tabelas: ordenação por clique no cabeçalho de qualquer coluna.
- Validação: campos obrigatórios, data no formato `AAAA-MM-DD`, email e senha no registo.
- Exceções: erros de validação e de base de dados são mostrados em diálogos, sem fechar a aplicação.

## Limitações conhecidas

Estas partes ainda não estão ligadas à interface (a lógica existe em `controller/` e `service/`):

- Gravar pagamentos associados a um processo aprovado.
- Gerar e exportar relatórios.
- Dashboard com estatísticas reais (`DashboardController`).
- Ecrã de gestão de utilizadores (listar, ativar/desativar, remover).

## Documentação Javadoc

```bash
mvn javadoc:javadoc
```

A documentação HTML é gerada em `target/site/apidocs/index.html`.
