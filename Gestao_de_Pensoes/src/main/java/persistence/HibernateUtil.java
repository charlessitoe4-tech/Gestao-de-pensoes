package persistence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import model.Beneficiario;
import model.Pensao;
import model.PensaoInvalidez;
import model.PensaoReduzida;
import model.PensaoSobrivivencia;
import model.PensaoVelhice;
import model.Pensionista;
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

        String urlConfigurada = System.getenv("GESTAO_PENSOES_DB_URL");
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
                    .addAnnotatedClass(Beneficiario.class)
                    .addAnnotatedClass(Pensionista.class)
                    .addAnnotatedClass(Pensao.class)
                    .addAnnotatedClass(PensaoInvalidez.class)
                    .addAnnotatedClass(PensaoReduzida.class)
                    .addAnnotatedClass(PensaoSobrivivencia.class)
                    .addAnnotatedClass(PensaoVelhice.class)
                    .buildMetadata()
                    .buildSessionFactory();
        } catch (RuntimeException ex) {
            StandardServiceRegistryBuilder.destroy(registro);
            throw new IllegalStateException("Não foi possível iniciar o Hibernate.", ex);
        }
    }
}
