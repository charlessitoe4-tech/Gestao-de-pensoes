package View;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import controller.LoginController;
import model.Utilizador;
import model.enums.NivelAcesso;
import persistence.HibernateUtil;
import service.RecuperacaoSenhaService;
import service.SistemaInicializacaoService;
import service.ValidacaoDadosMoçambique;

/** Ecrã de acesso, configuração inicial e recuperação de senha. */
public final class tela extends JFrame {

    private static final Logger LOGGER = Logger.getLogger(tela.class.getName());

    private final JTextField username = new JTextField(24);
    private final JPasswordField senha = new JPasswordField(24);
    private final JCheckBox mostrarSenha = new JCheckBox("Mostrar senha");
    private final JLabel mensagemEstado = new JLabel("Introduza as suas credenciais.");
    private final LoginController login = new LoginController();
    private final RecuperacaoSenhaService recuperacao = new RecuperacaoSenhaService();

    public tela() {
        setTitle("Gestão de Pensões | Acesso");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(760, 520));
        setSize(960, 640);
        setLocationRelativeTo(null);
        construirInterface();
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent evento) {
                HibernateUtil.encerrar();
            }
        });
    }

    private void construirInterface() {
        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(UiTheme.GREEN_PALE);

        JPanel marca = criarPainelMarca();
        JPanel acesso = criarPainelAcesso();
        raiz.add(marca, BorderLayout.WEST);
        raiz.add(acesso, BorderLayout.CENTER);
        setContentPane(raiz);
    }

    private JPanel criarPainelMarca() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBackground(UiTheme.GREEN_DARK);
        painel.setPreferredSize(new Dimension(360, 0));
        painel.setBorder(BorderFactory.createEmptyBorder(36, 38, 36, 38));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.insets = new Insets(0, 0, 18, 0);

        JLabel marca = new JLabel("GESTÃO DE PENSÕES");
        marca.setForeground(new Color(190, 245, 205));
        marca.setFont(marca.getFont().deriveFont(Font.BOLD, 15f));
        c.gridy = 0;
        painel.add(marca, c);

        JLabel titulo = new JLabel("<html>Segurança e cuidado<br>em cada etapa.</html>");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 32f));
        c.gridy = 1;
        c.insets = new Insets(0, 0, 14, 0);
        painel.add(titulo, c);

        JLabel descricao = new JLabel("<html>Uma forma simples e organizada de acompanhar<br>"
                + "beneficiários, pensionistas e processos.</html>");
        descricao.setForeground(new Color(220, 238, 225));
        descricao.setFont(descricao.getFont().deriveFont(15f));
        c.gridy = 2;
        c.insets = new Insets(0, 0, 0, 0);
        painel.add(descricao, c);

        c.gridy = 3;
        c.weighty = 1;
        painel.add(new JLabel(), c);

        JLabel rodape = new JLabel("Sistema de Gestão de Pensões");
        rodape.setForeground(new Color(190, 215, 198));
        rodape.setFont(rodape.getFont().deriveFont(12f));
        c.gridy = 4;
        c.weighty = 0;
        painel.add(rodape, c);
        return painel;
    }

    private JPanel criarPainelAcesso() {
        JPanel area = new JPanel(new GridBagLayout());
        area.setBackground(UiTheme.GREEN_PALE);
        area.setBorder(BorderFactory.createEmptyBorder(28, 30, 28, 30));

        JPanel cartao = new JPanel(new GridBagLayout());
        cartao.setBackground(Color.WHITE);
        cartao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiTheme.BORDER),
                BorderFactory.createEmptyBorder(30, 34, 28, 34)));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridwidth = 2;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 0, 8, 0);

        JLabel titulo = new JLabel("Bem-vindo(a)");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 27f));
        titulo.setForeground(UiTheme.TEXT);
        c.gridy = 0;
        cartao.add(titulo, c);

        JLabel subtitulo = new JLabel("Inicie sessão para continuar");
        subtitulo.setForeground(UiTheme.MUTED);
        subtitulo.setFont(subtitulo.getFont().deriveFont(14f));
        c.gridy = 1;
        c.insets = new Insets(0, 0, 28, 0);
        cartao.add(subtitulo, c);

        JLabel usernameLabel = new JLabel("Nome de utilizador");
        usernameLabel.setFont(usernameLabel.getFont().deriveFont(Font.BOLD, 13f));
        c.gridy = 2;
        c.insets = new Insets(0, 0, 7, 0);
        cartao.add(usernameLabel, c);

        c.gridy = 3;
        c.insets = new Insets(0, 0, 18, 0);
        cartao.add(username, c);

        JLabel senhaLabel = new JLabel("Senha");
        senhaLabel.setFont(senhaLabel.getFont().deriveFont(Font.BOLD, 13f));
        c.gridy = 4;
        c.insets = new Insets(0, 0, 7, 0);
        cartao.add(senhaLabel, c);

        c.gridy = 5;
        c.insets = new Insets(0, 0, 7, 0);
        cartao.add(senha, c);

        c.gridy = 6;
        c.insets = new Insets(0, 0, 20, 0);
        cartao.add(mostrarSenha, c);

        JButton entrar = new JButton("Iniciar sessão");
        entrar.setFont(entrar.getFont().deriveFont(Font.BOLD, 14f));
        entrar.addActionListener(evento -> autenticar());
        c.gridy = 7;
        c.ipady = 9;
        c.insets = new Insets(0, 0, 14, 0);
        cartao.add(entrar, c);

        JButton recuperar = new JButton("Esqueci-me da senha");
        recuperar.setForeground(UiTheme.GREEN_DARK);
        recuperar.setBackground(Color.WHITE);
        recuperar.addActionListener(evento -> recuperarSenha());
        c.gridy = 8;
        c.ipady = 2;
        c.insets = new Insets(0, 0, 18, 0);
        cartao.add(recuperar, c);

        mensagemEstado.setHorizontalAlignment(SwingConstants.CENTER);
        mensagemEstado.setForeground(UiTheme.MUTED);
        c.gridy = 9;
        c.insets = new Insets(0, 0, 0, 0);
        cartao.add(mensagemEstado, c);

        mostrarSenha.addActionListener(evento -> senha.setEchoChar(
                mostrarSenha.isSelected() ? (char) 0 : '•'));
        senha.setEchoChar('•');
        senha.addActionListener(evento -> autenticar());

        UiTheme.apply(cartao);
        UiTheme.styleDashboardCard(entrar);
        entrar.setBackground(UiTheme.GREEN);
        entrar.setForeground(Color.WHITE);
        entrar.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        recuperar.setBorderPainted(false);
        recuperar.setFocusPainted(false);

        GridBagConstraints areaConstraints = new GridBagConstraints();
        areaConstraints.fill = GridBagConstraints.HORIZONTAL;
        areaConstraints.weightx = 1;
        areaConstraints.insets = new Insets(0, 0, 0, 0);
        area.add(cartao, areaConstraints);
        return area;
    }

    private void autenticar() {
        char[] password = senha.getPassword();
        try {
            Optional<Utilizador> utilizador = login.autenticar(
                    username.getText().trim(), new String(password));
            if (utilizador.isEmpty()) {
                mostrarEstado("Credenciais inválidas ou conta inativa.", true);
                senha.setText("");
                senha.requestFocusInWindow();
                return;
            }
            new MainWindow(utilizador.orElseThrow()).setVisible(true);
            dispose();
        } catch (RuntimeException ex) {
            LOGGER.log(Level.SEVERE, "Não foi possível iniciar sessão.", ex);
            mostrarEstado("Falha ao iniciar sessão. Tente novamente.", true);
        } finally {
            Arrays.fill(password, '\0');
        }
    }

    private void prepararSistema() {
        SistemaInicializacaoService inicializacao = new SistemaInicializacaoService();
        inicializacao.assegurarPerfis();
        Map<String, NivelAcesso> emFalta = inicializacao.contasPrincipaisEmFalta();
        if (emFalta.isEmpty()) {
            inicializacao.assegurarContasPrincipais(Map.of(), Map.of());
            return;
        }
        Map<String, String> senhas = new LinkedHashMap<>();
        Map<String, String> emails = new LinkedHashMap<>();
        for (Map.Entry<String, NivelAcesso> conta : emFalta.entrySet()) {
            if (!configurarConta(inicializacao, conta, senhas, emails)) {
                throw new IllegalStateException("A configuração inicial foi cancelada.");
            }
        }
        inicializacao.assegurarContasPrincipais(senhas, emails);
        senhas.clear();
        emails.clear();
    }

    private boolean configurarConta(SistemaInicializacaoService inicializacao,
            Map.Entry<String, NivelAcesso> conta,
            Map<String, String> senhas, Map<String, String> emails) {
        JTextField email = new JTextField(22);
        JPasswordField novaSenha = inicializacao.necessitaSenhaInicial(conta.getKey())
                ? new JPasswordField(22) : null;
        JPasswordField confirmar = novaSenha == null ? null : new JPasswordField(22);
        JPanel formulario = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(5, 5, 5, 10);
        formulario.add(new JLabel("Conta:"), c);
        c.gridx = 1;
        formulario.add(new JLabel(conta.getKey() + " — " + conta.getValue()), c);
        c.gridx = 0;
        c.gridy++;
        formulario.add(new JLabel("Email de recuperação:"), c);
        c.gridx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        formulario.add(email, c);
        if (novaSenha != null) {
            c.gridx = 0;
            c.gridy++;
            formulario.add(new JLabel("Senha (mínimo 8 caracteres):"), c);
            c.gridx = 1;
            formulario.add(novaSenha, c);
            c.gridx = 0;
            c.gridy++;
            formulario.add(new JLabel("Confirmar senha:"), c);
            c.gridx = 1;
            formulario.add(confirmar, c);
        }
        UiTheme.apply(formulario);

        while (true) {
            int resultado = JOptionPane.showConfirmDialog(this, formulario,
                    "Configuração inicial", JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);
            if (resultado != JOptionPane.OK_OPTION) {
                return false;
            }
            char[] primeira = novaSenha == null ? new char[0] : novaSenha.getPassword();
            char[] segunda = confirmar == null ? new char[0] : confirmar.getPassword();
            try {
                if (!ValidacaoDadosMoçambique.emailValido(email.getText())) {
                    throw new IllegalArgumentException("Introduza um email válido.");
                }
                if (novaSenha != null && primeira.length < 8) {
                    throw new IllegalArgumentException("A senha deve ter pelo menos 8 caracteres.");
                }
                if (novaSenha != null && !Arrays.equals(primeira, segunda)) {
                    throw new IllegalArgumentException("As senhas não coincidem.");
                }
                emails.put(conta.getKey(), email.getText().trim());
                if (novaSenha != null) {
                    senhas.put(conta.getKey(), new String(primeira));
                }
                return true;
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "Dados inválidos", JOptionPane.WARNING_MESSAGE);
            } finally {
                Arrays.fill(primeira, '\0');
                Arrays.fill(segunda, '\0');
                if (novaSenha != null) {
                    novaSenha.setText("");
                    confirmar.setText("");
                }
            }
        }
    }

    private void recuperarSenha() {
        JTextField utilizador = new JTextField(username.getText().trim(), 22);
        JPanel pedido = new JPanel(new GridBagLayout());
        pedido.add(new JLabel("Nome de utilizador:"));
        pedido.add(utilizador);
        int resultado = JOptionPane.showConfirmDialog(this, pedido,
                "Solicitar código de recuperação", JOptionPane.OK_CANCEL_OPTION);
        if (resultado != JOptionPane.OK_OPTION) {
            return;
        }
        try {
            recuperacao.solicitarCodigo(utilizador.getText());
        } catch (RuntimeException ex) {
            LOGGER.log(Level.WARNING, "Falha ao enviar código de recuperação.", ex);
            JOptionPane.showMessageDialog(this,
                    "Não foi possível enviar o código. Verifique a configuração de email.",
                    "Recuperação de senha", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this,
                "Se a conta existir e tiver um email válido, será enviado um código.",
                "Recuperação de senha", JOptionPane.INFORMATION_MESSAGE);

        JTextField codigo = new JTextField(8);
        JPasswordField novaSenha = new JPasswordField(22);
        JPasswordField confirmar = new JPasswordField(22);
        JCheckBox mostrar = new JCheckBox("Mostrar senhas");
        mostrar.addActionListener(evento -> {
            char caractere = mostrar.isSelected() ? (char) 0 : '•';
            novaSenha.setEchoChar(caractere);
            confirmar.setEchoChar(caractere);
        });
        JPanel formulario = new JPanel(new GridBagLayout());
        adicionarCampo(formulario, "Código recebido:", codigo, 0);
        adicionarCampo(formulario, "Nova senha (mínimo 8 caracteres):", novaSenha, 1);
        adicionarCampo(formulario, "Confirmar senha:", confirmar, 2);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 3;
        c.anchor = GridBagConstraints.WEST;
        formulario.add(mostrar, c);
        UiTheme.apply(formulario);

        if (JOptionPane.showConfirmDialog(this, formulario, "Redefinir senha",
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
            return;
        }
        char[] primeira = novaSenha.getPassword();
        char[] segunda = confirmar.getPassword();
        try {
            if (!Arrays.equals(primeira, segunda)) {
                throw new IllegalArgumentException("As senhas não coincidem.");
            }
            recuperacao.redefinirSenha(utilizador.getText(), codigo.getText().trim(),
                    new String(primeira));
            JOptionPane.showMessageDialog(this, "Senha alterada com sucesso.",
                    "Recuperação de senha", JOptionPane.INFORMATION_MESSAGE);
        } catch (RuntimeException ex) {
            LOGGER.log(Level.WARNING, "Falha ao redefinir senha.", ex);
            JOptionPane.showMessageDialog(this,
                    "Não foi possível redefinir a senha. Verifique o código e tente novamente.",
                    "Recuperação de senha", JOptionPane.ERROR_MESSAGE);
        } finally {
            Arrays.fill(primeira, '\0');
            Arrays.fill(segunda, '\0');
        }
    }

    private void adicionarCampo(JPanel painel, String legenda,
            java.awt.Component campo, int linha) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = linha;
        c.gridx = 0;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(5, 5, 5, 10);
        painel.add(new JLabel(legenda), c);
        c.gridx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        painel.add(campo, c);
    }

    private void mostrarEstado(String texto, boolean erro) {
        mensagemEstado.setText(texto);
        mensagemEstado.setForeground(erro ? new Color(170, 35, 35) : UiTheme.MUTED);
    }

    private static void iniciar() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            LOGGER.log(Level.WARNING, "Não foi possível aplicar o tema Nimbus.", ex);
        }
        UiTheme.configureDialogs();
        tela janela = new tela();
        try {
            janela.prepararSistema();
            janela.setVisible(true);
        } catch (RuntimeException ex) {
            LOGGER.log(Level.SEVERE, "Falha ao preparar o sistema.", ex);
            JOptionPane.showMessageDialog(null,
                    "Não foi possível preparar o sistema: " + ex.getMessage(),
                    "Erro de arranque", JOptionPane.ERROR_MESSAGE);
            janela.dispose();
        }
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(tela::iniciar);
    }
}
