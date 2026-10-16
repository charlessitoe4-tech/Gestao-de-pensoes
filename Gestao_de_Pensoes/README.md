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

O comando `exec:java` abre diretamente a tela principal Swing (`View.telaPricipal`).
Esse ponto de entrada não executa autenticação nem configuração inicial das
contas principais. Para iniciar manualmente uma janela alternativa, pode também
executar a classe `View.telaCadastro` ou `View.tela`.

Por omissão, o Hibernate guarda os dados em H2 no ficheiro
`%USERPROFILE%\.gestao-pensoes\dados` no Windows (ou
`~/.gestao-pensoes/dados` noutros sistemas). Para usar outra base H2, defina a
propriedade de sistema ou variável de ambiente `GESTAO_PENSOES_DB_URL` antes de
iniciar.

Ao iniciar, o Hibernate cria as tabelas em falta e atualiza o esquema existente
sem apagar os registos (`hbm2ddl.auto=update`). Estão mapeadas as entidades de
beneficiários, pensionistas, pensões, pedidos de reforma, pagamentos, provas de
vida, prestações por morte, documentos, histórico, utilizadores, perfis,
permissões, relatórios e configurações. As subclasses de pensão e de prestação
por morte partilham, respetivamente, as tabelas `pensoes` e `prestacoes_morte`.

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
- **Acesso:** o login permite mostrar/ocultar a senha e solicitar recuperação
  através de um código de seis algarismos, válido por 10 minutos e limitado a
  cinco tentativas; há um intervalo mínimo de 60 segundos entre pedidos de
  códigos para a mesma conta. O username e o nome de utilizador são guardados com a
  primeira letra maiúscula. Senhas têm mínimo de 8 caracteres; BI aceita 12
  algarismos e NUIT 9 algarismos, ignorando espaços e hífens de formatação.

As tabelas suportam ordenação pelas colunas. Os formulários apresentam erros de
validação ou falhas de persistência sem ocultar a causa; os erros inesperados
também são registados no logger da aplicação.

## Perfis e controlo de acesso

| Perfil | Operações permitidas |
| --- | --- |
| Administrador | Consulta, criação, edição e remoção de dados; relatórios e gestão de utilizadores |
| Gestor | Consulta, criação, edição e remoção de dados operacionais; relatórios |
| Operador | Consulta, criação, edição, pagamentos e relatórios; sem remoção ou gestão de utilizadores |

O projeto mantém os serviços de autenticação e autorização para os fluxos que
os utilizem. A tela principal iniciada diretamente não aplica controlo de acesso
visual nem autentica o utilizador. As senhas geridas pelo serviço são guardadas
com PBKDF2-HMAC-SHA256 e os hashes SHA-256 legados são atualizados depois de
uma autenticação válida.

### Recuperação de senha por email

Para permitir o envio de códigos pelo serviço de recuperação de senha, configure
estas variáveis de ambiente antes de iniciar o programa:

| Variável | Descrição |
| --- | --- |
| `GESTAO_PENSOES_SMTP_HOST` | Servidor SMTP |
| `GESTAO_PENSOES_SMTP_PORT` | Porta SMTP (opcional; padrão 587) |
| `GESTAO_PENSOES_SMTP_USER` | Utilizador de autenticação SMTP |
| `GESTAO_PENSOES_SMTP_PASSWORD` | Senha ou senha de aplicação SMTP |
| `GESTAO_PENSOES_SMTP_FROM` | Endereço de email remetente |

O envio usa STARTTLS. Não coloque credenciais SMTP no código-fonte ou no
repositório. Se o serviço SMTP não estiver configurado, a aplicação apresenta
um erro explícito no fluxo de recuperação e regista os detalhes técnicos no log.

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
