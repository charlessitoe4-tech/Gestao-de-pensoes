package View;

import java.awt.GridLayout;
import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import controller.LoginController;
import model.Utilizador;
import persistence.HibernateUtil;
import service.SistemaInicializacaoService;

/** Arranque com configuração inicial e autenticação antes de abrir a aplicação. */
public final class AppLauncher {

    private static final Logger LOGGER = Logger.getLogger(AppLauncher.class.getName());

    private AppLauncher() {
    }

    public static void main(String[] args) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info
                    : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            LOGGER.log(Level.WARNING, "Não foi possível aplicar o tema Nimbus.", ex);
        }
        java.awt.EventQueue.invokeLater(AppLauncher::iniciar);
    }

    private static void iniciar() {
        try {
            SistemaInicializacaoService inicializacao = new SistemaInicializacaoService();
            inicializacao.assegurarPerfis();
            if (inicializacao.necessitaAdministradorInicial()
                    && !criarAdministrador(inicializacao)) {
                HibernateUtil.encerrar();
                return;
            }
            autenticar();
        } catch (RuntimeException ex) {
            LOGGER.log(Level.SEVERE, "Falha ao iniciar a aplicação.", ex);
            JOptionPane.showMessageDialog(null,
                    "Não foi possível iniciar a aplicação: " + mensagem(ex),
                    "Erro de arranque", JOptionPane.ERROR_MESSAGE);
            HibernateUtil.encerrar();
        }
    }

    private static boolean criarAdministrador(SistemaInicializacaoService inicializacao) {
        JTextField nome = new JTextField(22);
        JTextField username = new JTextField(22);
        JTextField email = new JTextField(22);
        JPasswordField senha = new JPasswordField(22);
        JPasswordField confirmacao = new JPasswordField(22);
        JPanel formulario = new JPanel(new GridLayout(0, 2, 8, 8));
        formulario.add(new JLabel("Nome completo:"));
        formulario.add(nome);
        formulario.add(new JLabel("Nome de utilizador:"));
        formulario.add(username);
        formulario.add(new JLabel("Email (opcional):"));
        formulario.add(email);
        formulario.add(new JLabel("Senha (mínimo 10 caracteres):"));
        formulario.add(senha);
        formulario.add(new JLabel("Confirmar senha:"));
        formulario.add(confirmacao);

        while (true) {
            int resultado = JOptionPane.showConfirmDialog(null, formulario,
                    "Primeira utilização — criar administrador",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (resultado != JOptionPane.OK_OPTION) {
                return false;
            }
            char[] primeira = senha.getPassword();
            char[] segunda = confirmacao.getPassword();
            try {
                if (!Arrays.equals(primeira, segunda)) {
                    throw new IllegalArgumentException("As senhas não coincidem.");
                }
                inicializacao.criarAdministradorInicial(nome.getText(), username.getText(),
                        email.getText(), new String(primeira));
                return true;
            } catch (RuntimeException ex) {
                mostrarErro(ex);
            } finally {
                Arrays.fill(primeira, '\0');
                Arrays.fill(segunda, '\0');
                senha.setText("");
                confirmacao.setText("");
            }
        }
    }

    private static void autenticar() {
        LoginController login = new LoginController();
        JTextField username = new JTextField(22);
        JPasswordField senha = new JPasswordField(22);
        JPanel formulario = new JPanel(new GridLayout(0, 2, 8, 8));
        formulario.add(new JLabel("Nome de utilizador:"));
        formulario.add(username);
        formulario.add(new JLabel("Senha:"));
        formulario.add(senha);
        while (true) {
            int resultado = JOptionPane.showConfirmDialog(null, formulario,
                    "Gestão de Pensões — iniciar sessão",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (resultado != JOptionPane.OK_OPTION) {
                HibernateUtil.encerrar();
                return;
            }
            char[] password = senha.getPassword();
            try {
                Optional<Utilizador> utilizador = login.autenticar(
                        username.getText(), new String(password));
                if (utilizador.isPresent()) {
                    new MainWindow(utilizador.orElseThrow()).setVisible(true);
                    return;
                }
                JOptionPane.showMessageDialog(null,
                        "Credenciais inválidas ou conta inativa.",
                        "Acesso recusado", JOptionPane.WARNING_MESSAGE);
                senha.setText("");
            } catch (RuntimeException ex) {
                mostrarErro(ex);
                senha.setText("");
            } finally {
                Arrays.fill(password, '\0');
            }
        }
    }

    private static void mostrarErro(RuntimeException ex) {
        LOGGER.log(Level.WARNING, "Operação de autenticação/configuração recusada.", ex);
        JOptionPane.showMessageDialog(null, mensagem(ex),
                "Dados inválidos", JOptionPane.ERROR_MESSAGE);
    }

    private static String mensagem(Throwable ex) {
        Throwable causa = ex;
        while (causa.getCause() != null && causa.getCause() != causa) {
            causa = causa.getCause();
        }
        return causa.getMessage() == null ? ex.getClass().getSimpleName() : causa.getMessage();
    }
}
