package persistence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import model.Beneficiario;
import model.Configuracao;
import model.Documento;
import model.Historico;
import model.Pagamento;
import model.Pensao;
import model.PensaoInvalidez;
import model.PensaoReduzida;
import model.PensaoSobrevivencia;
import model.PensaoVelhice;
import model.Pensionista;
import model.Perfil;
import model.Permissao;
import model.PrestacaoMorte;
import model.ProvaVida;
import model.Relatorio;
import model.SubsidioFuneral;
import model.SubsidioMorte;
import model.Utilizador;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;

/** Cria e gere a configuração Hibernate usada pela aplicação. */
public final class HibernateUtil {

    private static SessionFactory sessionFactory;

    private HibernateUtil() {
    }

    public static synchronized SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            sessionFactory = criarSessionFactory();
        }
        return sessionFactory;
    }

    public static synchronized void encerrar() {
        if (sessionFactory != null) {
            sessionFactory.close();
            sessionFactory = null;
        }
    }

    private static SessionFactory criarSessionFactory() {
        Map<String, Object> configuracao = new HashMap<>();
        configuracao.put(Environment.DRIVER, "org.h2.Driver");
        configuracao.put(Environment.USER, "sa");
        configuracao.put(Environment.PASS, "");
        configuracao.put(Environment.HBM2DDL_AUTO, "update");
        configuracao.put(Environment.SHOW_SQL, "false");

        String urlConfigurada = System.getProperty("GESTAO_PENSOES_DB_URL");
        if (urlConfigurada == null || urlConfigurada.isBlank()) {
            urlConfigurada = System.getenv("GESTAO_PENSOES_DB_URL");
        }
        if (urlConfigurada == null || urlConfigurada.isBlank()) {
            Path diretorioBase = Path.of(System.getProperty("user.home"), ".gestao-pensoes");
            try {
                Files.createDirectories(diretorioBase);
            } catch (IOException ex) {
                throw new IllegalStateException("Não foi possível criar a pasta da base de dados.", ex);
            }
            configuracao.put(Environment.URL,
                    "jdbc:h2:file:" + diretorioBase.resolve("dados").toAbsolutePath()
                            .toString().replace('\\', '/'));
        } else {
            configuracao.put(Environment.URL, urlConfigurada);
        }

        StandardServiceRegistry registro = new StandardServiceRegistryBuilder()
                .applySettings(configuracao)
                .build();
        try {
            return new MetadataSources(registro)
                    // Pessoa (subclasses)
                    .addAnnotatedClass(Beneficiario.class)
                    .addAnnotatedClass(Pensionista.class)
                    // Pensões (herança SINGLE_TABLE)
                    .addAnnotatedClass(Pensao.class)
                    .addAnnotatedClass(PensaoVelhice.class)
                    .addAnnotatedClass(PensaoInvalidez.class)
                    .addAnnotatedClass(PensaoSobrevivencia.class)
                    .addAnnotatedClass(PensaoReduzida.class)
                    // Prestações por morte (herança SINGLE_TABLE)
                    .addAnnotatedClass(PrestacaoMorte.class)
                    .addAnnotatedClass(SubsidioMorte.class)
                    .addAnnotatedClass(SubsidioFuneral.class)
                    // Operações
                    .addAnnotatedClass(Pagamento.class)
                    .addAnnotatedClass(ProvaVida.class)
                    // Documentos / Histórico
                    .addAnnotatedClass(Documento.class)
                    .addAnnotatedClass(Historico.class)
                    // Segurança
                    .addAnnotatedClass(Utilizador.class)
                    .addAnnotatedClass(Perfil.class)
                    .addAnnotatedClass(Permissao.class)
                    // Sistema
                    .addAnnotatedClass(Relatorio.class)
                    .addAnnotatedClass(Configuracao.class)
                    .buildMetadata()
                    .buildSessionFactory();
        } catch (RuntimeException ex) {
            StandardServiceRegistryBuilder.destroy(registro);
            throw new IllegalStateException("Não foi possível iniciar o Hibernate.", ex);
        }
    }
}