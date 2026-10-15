package View;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import controller.BeneficiarioController;
import controller.DashboardController;
import controller.PensionistaController;
import controller.PensaoController;
import dao.HibernatePerfilDAO;
import dao.PerfilDAO;
import model.Beneficiario;
import model.Pagamento;
import model.Pensionista;
import model.Pensao;
import model.Perfil;
import model.Relatorio;
import model.Utilizador;
import model.enums.EstadoCivil;
import model.enums.EstadoPensao;
import model.enums.FormaPagamento;
import model.enums.Genero;
import model.enums.NivelAcesso;
import model.enums.TipoPensao;
import persistence.HibernateUtil;
import service.AutorizacaoService;
import service.RelatorioService;
import service.UtilizadorService;

/** Interface de trabalho ligada aos serviços de domínio e à persistência Hibernate. */
public final class MainWindow extends javax.swing.JFrame {

    private static final Logger LOGGER = Logger.getLogger(MainWindow.class.getName());
    private static final String DASHBOARD = "dashboard";
    private static final String BENEFICIARIOS = "beneficiarios";
    private static final String PENSIONISTAS = "pensionistas";
    private static final String PENSOES = "pensaos";
    private static final String RELATORIOS = "relatorios";
    private static final String UTILIZADORES = "utilizadores";

    private final Utilizador utilizador;
    private final AutorizacaoService autorizacao;
    private final BeneficiarioController beneficiarios = new BeneficiarioController();
    private final PensionistaController pensionistas = new PensionistaController();
    private final PensaoController pensoes = new PensaoController();
    private final DashboardController dashboard = new DashboardController();
    private final RelatorioService relatorios = new RelatorioService();
    private final UtilizadorService utilizadores = new UtilizadorService();
    private final PerfilDAO perfilDAO = new HibernatePerfilDAO();
    private final JPanel conteudo = new JPanel(new CardLayout());

    private final DefaultTableModel beneficiariosModel = modelo(
            "ID", "Nome", "BI", "NUIT", "Telefone", "Email");
    private final JTable beneficiariosTable = new JTable(beneficiariosModel);
    private final JTextField pesquisaBeneficiarios = new JTextField(22);
    private final DefaultTableModel pensionistasModel = modelo(
            "ID", "N.º pensionista", "Nome", "BI", "Telefone", "Ativo");
    private final JTable pensionistasTable = new JTable(pensionistasModel);
    private final JTextField pesquisaPensionistas = new JTextField(22);
    private final DefaultTableModel pensoesModel = modelo(
            "ID", "Processo", "Tipo", "Pensionista", "Valor mensal", "Início", "Estado");
    private final JTable pensoesTable = new JTable(pensoesModel);
    private final JTextField pesquisaPensoes = new JTextField(18);
    private final JComboBox<Object> filtroTipo = new JComboBox<>();
    private final JComboBox<Object> filtroEstado = new JComboBox<>();
    private final DefaultTableModel utilizadoresModel = modelo(
            "ID", "Nome", "Username", "Perfil", "Ativo");
    private final JTable utilizadoresTable = new JTable(utilizadoresModel);
    private final DefaultTableModel relatoriosModel = modelo(
            "ID", "Título", "Tipo", "Gerado em", "Ficheiro");
    private final JTable relatoriosTable = new JTable(relatoriosModel);
    private final JLabel[] indicadores = new JLabel[4];
    private final JPanel painelDashboard = new JPanel(new BorderLayout(12, 12));

    public MainWindow(Utilizador utilizador) {
        this.utilizador = Objects.requireNonNull(utilizador, "O utilizador é obrigatório.");
        this.autorizacao = new AutorizacaoService(utilizador);
        setTitle("Gestão de Pensões");
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1050, 680));
        setSize(1280, 820);
        setLocationRelativeTo(null);
        construirInterface();
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evento) {
                HibernateUtil.encerrar();
            }
        });
        atualizarDashboard();
    }

    private void construirInterface() {
        JPanel raiz = new JPanel(new BorderLayout());
        JPanel topo = new JPanel(new BorderLayout());
        topo.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        JLabel marca = new JLabel("GESTÃO DE PENSÕES");
        marca.setFont(marca.getFont().deriveFont(java.awt.Font.BOLD, 18f));
        JLabel sessao = new JLabel(utilizador.getNome() + "  ·  "
                + utilizador.getPerfil().getNome());
        JButton terminarSessao = new JButton("Terminar sessão");
        terminarSessao.addActionListener(evento -> {
            dispose();
            HibernateUtil.encerrar();
            AppLauncher.main(new String[0]);
        });
        JPanel conta = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        conta.add(sessao);
        conta.add(terminarSessao);
        topo.add(marca, BorderLayout.WEST);
        topo.add(conta, BorderLayout.EAST);

        JPanel navegacao = new JPanel(new GridLayout(0, 1, 0, 8));
        navegacao.setBorder(BorderFactory.createEmptyBorder(18, 12, 12, 12));
        navegacao.setPreferredSize(new java.awt.Dimension(210, 0));
        adicionarNavegacao(navegacao, "Painel principal", DASHBOARD);
        adicionarNavegacao(navegacao, "Beneficiários", BENEFICIARIOS);
        adicionarNavegacao(navegacao, "Pensionistas", PENSIONISTAS);
        adicionarNavegacao(navegacao, "Pensões e pagamentos", PENSOES);
        adicionarNavegacao(navegacao, "Relatórios", RELATORIOS);
        if (autorizacao.podeGerirUtilizadores()) {
            adicionarNavegacao(navegacao, "Utilizadores", UTILIZADORES);
        }

        conteudo.add(criarDashboard(), DASHBOARD);
        conteudo.add(criarPainelBeneficiarios(), BENEFICIARIOS);
        conteudo.add(criarPainelPensionistas(), PENSIONISTAS);
        conteudo.add(criarPainelPensoes(), PENSOES);
        conteudo.add(criarPainelRelatorios(), RELATORIOS);
        if (autorizacao.podeGerirUtilizadores()) {
            conteudo.add(criarPainelUtilizadores(), UTILIZADORES);
        }

        raiz.add(topo, BorderLayout.NORTH);
        raiz.add(navegacao, BorderLayout.WEST);
        raiz.add(conteudo, BorderLayout.CENTER);
        setContentPane(raiz);
        mostrar(DASHBOARD);
    }

    private void adicionarNavegacao(JPanel painel, String texto, String cartao) {
        JButton botao = new JButton(texto);
        botao.setHorizontalAlignment(JButton.LEFT);
        botao.addActionListener(evento -> mostrar(cartao));
        painel.add(botao);
    }

    private void mostrar(String cartao) {
        ((CardLayout) conteudo.getLayout()).show(conteudo, cartao);
        switch (cartao) {
            case DASHBOARD -> atualizarDashboard();
            case BENEFICIARIOS -> atualizarBeneficiarios();
            case PENSIONISTAS -> atualizarPensionistas();
            case PENSOES -> atualizarPensoes();
            case RELATORIOS -> atualizarRelatorios();
            case UTILIZADORES -> atualizarUtilizadores();
            default -> throw new IllegalArgumentException("Ecrã desconhecido: " + cartao);
        }
    }

    private JPanel criarDashboard() {
        painelDashboard.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.add(titulo("Painel principal", "Resumo atualizado dos registos persistidos."), BorderLayout.WEST);
        JButton atualizar = new JButton("Atualizar");
        atualizar.addActionListener(evento -> executar(this::atualizarDashboard));
        cabecalho.add(atualizar, BorderLayout.EAST);
        JPanel cartoes = new JPanel(new GridLayout(2, 2, 14, 14));
        String[] rotulos = {"Beneficiários", "Pensionistas", "Pensões registadas", "Total mensal ativo"};
        for (int i = 0; i < indicadores.length; i++) {
            JPanel cartao = new JPanel(new BorderLayout());
            cartao.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new java.awt.Color(215, 223, 229)),
                    BorderFactory.createEmptyBorder(20, 20, 20, 20)));
            JLabel nome = new JLabel(rotulos[i]);
            nome.setFont(nome.getFont().deriveFont(java.awt.Font.PLAIN, 15f));
            indicadores[i] = new JLabel("—");
            indicadores[i].setFont(indicadores[i].getFont().deriveFont(java.awt.Font.BOLD, 28f));
            cartao.add(nome, BorderLayout.NORTH);
            cartao.add(indicadores[i], BorderLayout.CENTER);
            cartoes.add(cartao);
        }
        painelDashboard.add(cabecalho, BorderLayout.NORTH);
        painelDashboard.add(cartoes, BorderLayout.CENTER);
        return painelDashboard;
    }

    private void atualizarDashboard() {
        indicadores[0].setText(Integer.toString(dashboard.contarBeneficiarios()));
        indicadores[1].setText(Integer.toString(pensionistas.listar().size()));
        indicadores[2].setText(Integer.toString(dashboard.contarPensoes()));
        indicadores[3].setText(dashboard.totalMensalAtivo().toPlainString());
    }

    private JPanel criarPainelBeneficiarios() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(20, 22, 20, 22));
        painel.add(titulo("Beneficiários", "Pesquisar, ordenar, registar e atualizar beneficiários."), BorderLayout.NORTH);
        painel.add(new JScrollPane(beneficiariosTable), BorderLayout.CENTER);
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT));
        barra.add(new JLabel("Pesquisa:"));
        barra.add(pesquisaBeneficiarios);
        adicionarBotao(barra, "Novo", autorizacao.podeEditarDados(), () -> editarBeneficiario(null));
        adicionarBotao(barra, "Editar", autorizacao.podeEditarDados(),
                () -> editarBeneficiario(selecionarId(beneficiariosTable)));
        adicionarBotao(barra, "Eliminar", autorizacao.podeEliminarDados(),
                () -> eliminarBeneficiario(selecionarId(beneficiariosTable)));
        painel.add(barra, BorderLayout.SOUTH);
        pesquisaBeneficiarios.getDocument().addDocumentListener(documentListener(
                () -> executar(this::atualizarBeneficiarios)));
        return painel;
    }

    private void atualizarBeneficiarios() {
        preencher(beneficiariosModel, beneficiarios.pesquisar(pesquisaBeneficiarios.getText()).stream()
                .map(b -> new Object[]{b.getId(), b.getNomeCompleto(), b.getNumeroBI(),
                    b.getNuit(), b.getTelefone(), b.getEmail()})
                .toList());
        ordenar(beneficiariosTable);
    }

    private void editarBeneficiario(Long id) {
        Beneficiario atual = id == null ? null : beneficiarios.buscarPorId(id)
                .orElseThrow(() -> new IllegalStateException("O beneficiário selecionado já não existe."));
        JTextField nome = campo(atual == null ? "" : atual.getNomeCompleto());
        JTextField bi = campo(valor(atual == null ? null : atual.getNumeroBI()));
        JTextField nuit = campo(valor(atual == null ? null : atual.getNuit()));
        JTextField nascimento = campo(atual == null ? "" : data(atual.getDataNascimento()));
        JComboBox<Genero> genero = combo(Genero.values(), atual == null ? null : atual.getGenero());
        JComboBox<EstadoCivil> civil = combo(EstadoCivil.values(), atual == null ? null : atual.getEstadoCivil());
        JTextField telefone = campo(valor(atual == null ? null : atual.getTelefone()));
        JTextField email = campo(valor(atual == null ? null : atual.getEmail()));
        JTextField endereco = campo(valor(atual == null ? null : atual.getEndereco()));
        JTextField nacionalidade = campo(valor(atual == null ? null : atual.getNacionalidade()));
        JTextField provincia = campo(valor(atual == null ? null : atual.getProvincia()));
        JTextField distrito = campo(valor(atual == null ? null : atual.getDistrito()));
        JPanel formulario = formulario(
                "Nome completo", nome, "BI", bi, "NUIT", nuit, "Nascimento (AAAA-MM-DD)", nascimento,
                "Sexo", genero, "Estado civil", civil, "Telefone", telefone, "Email", email,
                "Endereço", endereco, "Nacionalidade", nacionalidade, "Província", provincia,
                "Distrito", distrito);
        if (JOptionPane.showConfirmDialog(this, new JScrollPane(formulario),
                id == null ? "Novo beneficiário" : "Editar beneficiário",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) {
            return;
        }
        executar(() -> {
            beneficiarios.guardar(id, nome.getText(), bi.getText(), nuit.getText(),
                    nascimento.getText(), valorEnum(genero), valorEnum(civil), telefone.getText(),
                    email.getText(), endereco.getText(), nacionalidade.getText(),
                    provincia.getText(), distrito.getText());
            atualizarBeneficiarios();
            atualizarDashboard();
        });
    }

    private void eliminarBeneficiario(Long id) {
        if (id == null) {
            return;
        }
        if (confirmar("Eliminar o beneficiário selecionado?")) {
            executar(() -> {
                if (!beneficiarios.remover(id)) {
                    throw new IllegalStateException("O beneficiário já não existe.");
                }
                atualizarBeneficiarios();
                atualizarDashboard();
            });
        }
    }

    private JPanel criarPainelPensionistas() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(20, 22, 20, 22));
        painel.add(titulo("Pensionistas", "Gestão dos titulares associados às pensões."), BorderLayout.NORTH);
        painel.add(new JScrollPane(pensionistasTable), BorderLayout.CENTER);
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT));
        barra.add(new JLabel("Pesquisa:"));
        barra.add(pesquisaPensionistas);
        adicionarBotao(barra, "Novo", autorizacao.podeEditarDados(), () -> editarPensionista(null));
        adicionarBotao(barra, "Editar", autorizacao.podeEditarDados(),
                () -> editarPensionista(selecionarId(pensionistasTable)));
        adicionarBotao(barra, "Eliminar", autorizacao.podeEliminarDados(),
                () -> eliminarPensionista(selecionarId(pensionistasTable)));
        painel.add(barra, BorderLayout.SOUTH);
        pesquisaPensionistas.getDocument().addDocumentListener(documentListener(
                () -> executar(this::atualizarPensionistas)));
        return painel;
    }

    private void atualizarPensionistas() {
        preencher(pensionistasModel, pensionistas.pesquisar(pesquisaPensionistas.getText()).stream()
                .map(p -> new Object[]{p.getId(), p.getNumeroPensionista(), p.getNomeCompleto(),
                    p.getNumeroBI(), p.getTelefone(), p.isAtivo() ? "Sim" : "Não"})
                .toList());
        ordenar(pensionistasTable);
    }

    private void editarPensionista(Long id) {
        Pensionista atual = id == null ? null : pensionistas.listar().stream()
                .filter(p -> p.getId().equals(id)).findFirst()
                .orElseThrow(() -> new IllegalStateException("O pensionista selecionado já não existe."));
        JTextField numero = campo(atual == null ? "" : valor(atual.getNumeroPensionista()));
        JTextField nome = campo(atual == null ? "" : atual.getNomeCompleto());
        JTextField bi = campo(valor(atual == null ? null : atual.getNumeroBI()));
        JTextField nuit = campo(valor(atual == null ? null : atual.getNuit()));
        JTextField nascimento = campo(atual == null ? "" : data(atual.getDataNascimento()));
        JComboBox<Genero> genero = combo(Genero.values(), atual == null ? null : atual.getGenero());
        JComboBox<EstadoCivil> civil = combo(EstadoCivil.values(), atual == null ? null : atual.getEstadoCivil());
        JTextField telefone = campo(valor(atual == null ? null : atual.getTelefone()));
        JTextField email = campo(valor(atual == null ? null : atual.getEmail()));
        JTextField endereco = campo(valor(atual == null ? null : atual.getEndereco()));
        JTextField provincia = campo(valor(atual == null ? null : atual.getProvincia()));
        JTextField distrito = campo(valor(atual == null ? null : atual.getDistrito()));
        JTextField nib = campo(valor(atual == null ? null : atual.getNib()));
        JTextField banco = campo(valor(atual == null ? null : atual.getBanco()));
        JTextField aposentadoria = campo(atual == null ? "" : data(atual.getDataAposentadoria()));
        JCheckBox ativo = new JCheckBox("Ativo", atual == null || atual.isAtivo());
        JPanel formulario = formulario(
                "N.º pensionista", numero, "Nome completo", nome, "BI", bi, "NUIT", nuit,
                "Nascimento (AAAA-MM-DD)", nascimento, "Sexo", genero, "Estado civil", civil,
                "Telefone", telefone, "Email", email, "Endereço", endereco,
                "Província", provincia, "Distrito", distrito, "NIB", nib, "Banco", banco,
                "Aposentadoria (AAAA-MM-DD)", aposentadoria, "Estado", ativo);
        if (JOptionPane.showConfirmDialog(this, new JScrollPane(formulario),
                id == null ? "Novo pensionista" : "Editar pensionista",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) {
            return;
        }
        executar(() -> {
            pensionistas.guardar(id, nome.getText(), bi.getText(), nuit.getText(), nascimento.getText(),
                    valorEnum(genero), valorEnum(civil), telefone.getText(), email.getText(),
                    endereco.getText(), provincia.getText(), distrito.getText(), numero.getText(),
                    nib.getText(), banco.getText(), aposentadoria.getText(), ativo.isSelected());
            atualizarPensionistas();
            atualizarDashboard();
            atualizarPensoes();
        });
    }

    private void eliminarPensionista(Long id) {
        if (id == null || !confirmar("Eliminar o pensionista selecionado?")) {
            return;
        }
        executar(() -> {
            if (!pensionistas.remover(id)) {
                throw new IllegalStateException("O pensionista já não existe.");
            }
            atualizarPensionistas();
            atualizarDashboard();
            atualizarPensoes();
        });
    }

    private JPanel criarPainelPensoes() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(20, 22, 20, 22));
        painel.add(titulo("Pensões e pagamentos",
                "Pesquise, filtre, ordene e acompanhe o ciclo de vida das pensões."), BorderLayout.NORTH);
        painel.add(new JScrollPane(pensoesTable), BorderLayout.CENTER);
        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filtros.add(new JLabel("Pesquisa:"));
        filtros.add(pesquisaPensoes);
        filtros.add(new JLabel("Tipo:"));
        filtroTipo.addItem("Todos");
        for (TipoPensao tipo : TipoPensao.values()) {
            filtroTipo.addItem(tipo);
        }
        filtros.add(filtroTipo);
        filtros.add(new JLabel("Estado:"));
        filtroEstado.addItem("Todos");
        for (EstadoPensao estado : EstadoPensao.values()) {
            filtroEstado.addItem(estado);
        }
        filtros.add(filtroEstado);
        adicionarBotao(filtros, "Novo", autorizacao.podeEditarDados(), () -> editarPensao(null));
        adicionarBotao(filtros, "Editar", autorizacao.podeEditarDados(),
                () -> editarPensao(selecionarId(pensoesTable)));
        adicionarBotao(filtros, "Eliminar", autorizacao.podeEliminarDados(),
                () -> eliminarPensao(selecionarId(pensoesTable)));
        adicionarBotao(filtros, "Aprovar", autorizacao.podeEditarDados(),
                () -> acaoPensao(selecionarId(pensoesTable), pensoes::aprovar));
        adicionarBotao(filtros, "Suspender", autorizacao.podeEditarDados(),
                () -> acaoPensao(selecionarId(pensoesTable), pensoes::suspender));
        adicionarBotao(filtros, "Reativar", autorizacao.podeEditarDados(),
                () -> acaoPensao(selecionarId(pensoesTable), pensoes::reativar));
        adicionarBotao(filtros, "Cancelar", autorizacao.podeEditarDados(),
                () -> acaoPensao(selecionarId(pensoesTable), pensoes::cancelar));
        adicionarBotao(filtros, "Arquivar", autorizacao.podeEditarDados(),
                () -> acaoPensao(selecionarId(pensoesTable), pensoes::arquivar));
        adicionarBotao(filtros, "Registar pagamento", autorizacao.podeEditarDados(),
                () -> registarPagamento(selecionarId(pensoesTable)));
        painel.add(filtros, BorderLayout.SOUTH);
        DocumentListener listener = documentListener(() -> executar(this::atualizarPensoes));
        pesquisaPensoes.getDocument().addDocumentListener(listener);
        filtroTipo.addActionListener(evento -> executar(this::atualizarPensoes));
        filtroEstado.addActionListener(evento -> executar(this::atualizarPensoes));
        return painel;
    }

    private void atualizarPensoes() {
        Object tipoSelecionado = filtroTipo.getSelectedItem();
        Object estadoSelecionado = filtroEstado.getSelectedItem();
        String tipo = tipoSelecionado instanceof TipoPensao valor ? valor.name() : null;
        String estado = estadoSelecionado instanceof EstadoPensao valor ? valor.name() : null;
        List<Pensao> lista = pensoes.pesquisar(pesquisaPensoes.getText(), tipo, estado, null);
        preencher(pensoesModel, lista.stream().map(p -> new Object[]{
            p.getId(), p.getNumeroProcesso(), p.getTipo(), nomePensionista(p.getPensionistaId()),
            p.getValorMensal(), p.getDataInicio(), p.getEstado()
        }).toList());
        ordenar(pensoesTable);
    }

    private String nomePensionista(Long id) {
        return pensionistas.listar().stream().filter(p -> p.getId().equals(id))
                .map(Pensionista::getNomeCompleto).findFirst()
                .orElse(id == null ? "" : "ID " + id);
    }

    private void editarPensao(Long id) {
        List<Pensionista> existentes = pensionistas.listar();
        if (existentes.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Registe primeiro um pensionista para poder associar uma pensão.",
                    "Pensionista necessário", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Pensao atual = id == null ? null : pensoes.buscarPorId(id)
                .orElseThrow(() -> new IllegalStateException("A pensão selecionada já não existe."));
        JTextField processo = campo(atual == null ? "" : valor(atual.getNumeroProcesso()));
        JComboBox<TipoPensao> tipo = combo(TipoPensao.values(), atual == null ? null : atual.getTipo());
        JComboBox<Pensionista> pensionista = new JComboBox<>(existentes.toArray(Pensionista[]::new));
        if (atual != null) {
            existentes.stream().filter(p -> p.getId().equals(atual.getPensionistaId()))
                    .findFirst().ifPresent(pensionista::setSelectedItem);
        }
        JTextField valorMensal = campo(atual == null ? "" : atual.getValorMensal().toPlainString());
        JTextField inicio = campo(atual == null ? LocalDate.now().toString() : data(atual.getDataInicio()));
        JTextArea observacoes = new JTextArea(atual == null ? "" : valor(atual.getObservacoes()), 3, 20);
        JPanel formulario = formulario("N.º processo", processo, "Tipo", tipo,
                "Pensionista", pensionista, "Valor mensal", valorMensal,
                "Data de início (AAAA-MM-DD)", inicio, "Observações", new JScrollPane(observacoes));
        if (JOptionPane.showConfirmDialog(this, new JScrollPane(formulario),
                id == null ? "Nova pensão" : "Editar pensão",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) {
            return;
        }
        executar(() -> {
            Pensionista escolhido = (Pensionista) pensionista.getSelectedItem();
            if (escolhido == null) {
                throw new IllegalArgumentException("Selecione o pensionista titular.");
            }
            pensoes.guardar(id, processo.getText(), (TipoPensao) tipo.getSelectedItem(),
                    escolhido.getId(), new BigDecimal(valorMensal.getText().trim()),
                    LocalDate.parse(inicio.getText().trim()), observacoes.getText());
            atualizarPensoes();
            atualizarDashboard();
        });
    }

    private void eliminarPensao(Long id) {
        if (id == null || !confirmar("Eliminar a pensão selecionada?")) {
            return;
        }
        executar(() -> {
            if (!pensoes.remover(id)) {
                throw new IllegalStateException("A pensão já não existe.");
            }
            atualizarPensoes();
            atualizarDashboard();
        });
    }

    private void acaoPensao(Long id, java.util.function.Function<Long, Pensao> acao) {
        if (id == null) {
            return;
        }
        executar(() -> {
            acao.apply(id);
            atualizarPensoes();
            atualizarDashboard();
        });
    }

    private void registarPagamento(Long pensaoId) {
        if (pensaoId == null) {
            return;
        }
        JTextField periodo = campo(YearMonth.now().toString());
        JComboBox<FormaPagamento> forma = combo(FormaPagamento.values(), null);
        JPanel formulario = formulario("Período (AAAA-MM)", periodo, "Forma de pagamento", forma);
        if (JOptionPane.showConfirmDialog(this, formulario, "Registar pagamento",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) {
            return;
        }
        executar(() -> {
            Pagamento pagamento = pensoes.registarPagamento(pensaoId,
                    YearMonth.parse(periodo.getText().trim()), (FormaPagamento) forma.getSelectedItem());
            JOptionPane.showMessageDialog(this, "Pagamento registado: " + pagamento.getId(),
                    "Pagamento", JOptionPane.INFORMATION_MESSAGE);
            atualizarDashboard();
        });
    }

    private JPanel criarPainelRelatorios() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(20, 22, 20, 22));
        painel.add(titulo("Relatórios", "Gere um CSV do estado atual e consulte o histórico."), BorderLayout.NORTH);
        painel.add(new JScrollPane(relatoriosTable), BorderLayout.CENTER);
        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        adicionarBotao(acoes, "Exportar resumo CSV", true, this::exportarRelatorio);
        painel.add(acoes, BorderLayout.SOUTH);
        return painel;
    }

    private void exportarRelatorio() {
        JFileChooser seletor = new JFileChooser();
        seletor.setDialogTitle("Guardar relatório CSV");
        seletor.setSelectedFile(new java.io.File("relatorio-pensoes.csv"));
        if (seletor.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        executar(() -> {
            Path ficheiro = seletor.getSelectedFile().toPath();
            String csv = construirCsv();
            try {
                Files.writeString(ficheiro, csv, StandardCharsets.UTF_8);
            } catch (IOException ex) {
                throw new IllegalStateException("Não foi possível guardar o relatório.", ex);
            }
            Relatorio relatorio = new Relatorio();
            relatorio.setTitulo("Resumo de pensões");
            relatorio.setTipo("CSV");
            relatorio.setUtilizadorId(utilizador.getId());
            relatorio.setParametros("Beneficiários, pensionistas e pensões");
            relatorio.setCaminhoFicheiro(ficheiro.toAbsolutePath().toString());
            relatorios.gerar(relatorio);
            atualizarRelatorios();
            JOptionPane.showMessageDialog(this, "Relatório guardado em:\n" + ficheiro.toAbsolutePath(),
                    "Relatório gerado", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    private String construirCsv() {
        StringBuilder csv = new StringBuilder("tipo,id,nome_ou_processo,referencia,valor_mensal,estado\n");
        for (Beneficiario b : beneficiarios.listar()) {
            csv.append("beneficiario,").append(b.getId()).append(',')
                    .append(celula(b.getNomeCompleto())).append(',')
                    .append(celula(b.getNumeroBI())).append(",,\n");
        }
        for (Pensao p : pensoes.listar()) {
            csv.append("pensao,").append(p.getId()).append(',')
                    .append(celula(p.getNumeroProcesso())).append(',')
                    .append(celula(nomePensionista(p.getPensionistaId()))).append(',')
                    .append(p.getValorMensal()).append(',').append(p.getEstado()).append('\n');
        }
        return csv.toString();
    }

    private String celula(String valor) {
        String escapado = valor == null ? "" : valor.replace("\"", "\"\"");
        return "\"" + escapado + "\"";
    }

    private void atualizarRelatorios() {
        preencher(relatoriosModel, relatorios.listar().stream()
                .map(r -> new Object[]{r.getId(), r.getTitulo(), r.getTipo(),
                    r.getDataGeracao(), r.getCaminhoFicheiro()})
                .toList());
        ordenar(relatoriosTable);
    }

    private JPanel criarPainelUtilizadores() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(20, 22, 20, 22));
        painel.add(titulo("Utilizadores", "Apenas administradores podem criar e ativar contas."), BorderLayout.NORTH);
        painel.add(new JScrollPane(utilizadoresTable), BorderLayout.CENTER);
        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        adicionarBotao(acoes, "Novo utilizador", true, this::criarUtilizador);
        adicionarBotao(acoes, "Ativar/desativar", true,
                () -> alternarUtilizador(selecionarId(utilizadoresTable)));
        painel.add(acoes, BorderLayout.SOUTH);
        return painel;
    }

    private void atualizarUtilizadores() {
        if (!autorizacao.podeGerirUtilizadores()) {
            throw new SecurityException("Não tem permissão para gerir utilizadores.");
        }
        preencher(utilizadoresModel, utilizadores.listar().stream()
                .map(u -> new Object[]{u.getId(), u.getNome(), u.getUsername(),
                    u.getPerfil() == null ? "" : u.getPerfil().getNome(),
                    u.isAtivo() ? "Sim" : "Não"})
                .toList());
        ordenar(utilizadoresTable);
    }

    private void criarUtilizador() {
        JTextField nome = campo("");
        JTextField username = campo("");
        JTextField email = campo("");
        JPasswordField senha = new JPasswordField(18);
        List<Perfil> perfisDisponiveis = perfilDAO.listarTodos().stream()
                .filter(p -> p.getNivelAcesso() == NivelAcesso.ADMINISTRADOR
                        || p.getNivelAcesso() == NivelAcesso.GESTOR
                        || p.getNivelAcesso() == NivelAcesso.OPERADOR)
                .toList();
        JComboBox<Perfil> perfil = new JComboBox<>(perfisDisponiveis.toArray(Perfil[]::new));
        JPanel formulario = formulario("Nome", nome, "Username", username, "Email", email,
                "Senha (mínimo 10 caracteres)", senha, "Perfil", perfil);
        if (JOptionPane.showConfirmDialog(this, formulario, "Criar utilizador",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) {
            return;
        }
        char[] palavraPasse = senha.getPassword();
        try {
            executar(() -> {
                utilizadores.registar(nome.getText(), username.getText(), email.getText(),
                        new String(palavraPasse), (Perfil) perfil.getSelectedItem());
                atualizarUtilizadores();
            });
        } finally {
            java.util.Arrays.fill(palavraPasse, '\0');
            senha.setText("");
        }
    }

    private void alternarUtilizador(Long id) {
        if (id == null) {
            return;
        }
        executar(() -> {
            Utilizador alvo = utilizadores.buscarPorId(id)
                    .orElseThrow(() -> new IllegalStateException("Utilizador não encontrado."));
            if (Objects.equals(alvo.getId(), utilizador.getId()) && alvo.isAtivo()) {
                throw new IllegalArgumentException("Não pode desativar a sua própria conta.");
            }
            alvo.setAtivo(!alvo.isAtivo());
            utilizadores.guardar(alvo);
            atualizarUtilizadores();
        });
    }

    private void adicionarBotao(JPanel painel, String texto, boolean permitido, Runnable acao) {
        JButton botao = new JButton(texto);
        botao.setEnabled(permitido);
        botao.addActionListener(evento -> executar(acao));
        painel.add(botao);
    }

    private void executar(Runnable acao) {
        try {
            acao.run();
        } catch (RuntimeException ex) {
            LOGGER.log(Level.SEVERE, "A operação solicitada não foi concluída.", ex);
            JOptionPane.showMessageDialog(this, mensagem(ex),
                    "Operação não concluída", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String mensagem(Throwable erro) {
        Throwable causa = erro;
        while (causa.getCause() != null && causa.getCause() != causa) {
            causa = causa.getCause();
        }
        String mensagem = causa.getMessage();
        if (mensagem == null || mensagem.isBlank()) {
            return "Ocorreu um erro inesperado. Consulte o registo da aplicação.";
        }
        return mensagem;
    }

    private boolean confirmar(String pergunta) {
        return JOptionPane.showConfirmDialog(this, pergunta, "Confirmar",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    private Long selecionarId(JTable tabela) {
        int linhaVista = tabela.getSelectedRow();
        if (linhaVista < 0) {
            JOptionPane.showMessageDialog(this, "Selecione primeiro um registo.",
                    "Seleção necessária", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        int linhaModelo = tabela.convertRowIndexToModel(linhaVista);
        return ((Number) tabela.getModel().getValueAt(linhaModelo, 0)).longValue();
    }

    private JPanel titulo(String texto, String descricao) {
        JPanel painel = new JPanel(new GridLayout(0, 1, 0, 4));
        JLabel titulo = new JLabel(texto);
        titulo.setFont(titulo.getFont().deriveFont(java.awt.Font.BOLD, 22f));
        painel.add(titulo);
        painel.add(new JLabel(descricao));
        painel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        return painel;
    }

    private static DefaultTableModel modelo(String... colunas) {
        return new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private static void preencher(DefaultTableModel modelo, List<Object[]> linhas) {
        modelo.setRowCount(0);
        for (Object[] linha : linhas) {
            modelo.addRow(linha);
        }
    }

    private static void ordenar(JTable tabela) {
        TableRowSorter<?> sorter = new TableRowSorter<>(tabela.getModel());
        tabela.setRowSorter(sorter);
        sorter.setSortKeys(List.of(new RowSorter.SortKey(0, SortOrder.ASCENDING)));
    }

    private static JPanel formulario(Object... campos) {
        JPanel painel = new JPanel(new GridLayout(0, 2, 8, 8));
        for (int i = 0; i < campos.length; i += 2) {
            painel.add(new JLabel((String) campos[i] + ":"));
            painel.add((java.awt.Component) campos[i + 1]);
        }
        painel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        return painel;
    }

    private static JTextField campo(String valor) {
        return new JTextField(valor, 20);
    }

    private static <T> JComboBox<T> combo(T[] opcoes, T selecionado) {
        JComboBox<T> combo = new JComboBox<>(opcoes);
        if (selecionado != null) {
            combo.setSelectedItem(selecionado);
        }
        return combo;
    }

    private static String valorEnum(JComboBox<? extends Enum<?>> combo) {
        Enum<?> valor = (Enum<?>) combo.getSelectedItem();
        return valor == null ? null : valor.name();
    }

    private static String valor(String texto) {
        return texto == null ? "" : texto;
    }

    private static String data(LocalDate data) {
        return data == null ? "" : data.toString();
    }

    private static DocumentListener documentListener(Runnable acao) {
        return new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent evento) {
                acao.run();
            }

            @Override
            public void removeUpdate(DocumentEvent evento) {
                acao.run();
            }

            @Override
            public void changedUpdate(DocumentEvent evento) {
                acao.run();
            }
        };
    }
}
