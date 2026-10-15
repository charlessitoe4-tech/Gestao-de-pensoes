package persistence;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
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
import model.PedidoReforma;
import model.Pensionista;
import model.Perfil;
import model.Permissao;
import model.PrestacaoMorte;
import model.ProvaVida;
import model.Relatorio;
import model.SubsidioFuneral;
import model.SubsidioMorte;
import model.Utilizador;
import org.hibernate.Session;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class HibernateSchemaTest {

    @AfterEach
    void encerrarHibernate() {
        HibernateUtil.encerrar();
        System.clearProperty("GESTAO_PENSOES_DB_URL");
    }

    @Test
    void criaTabelasParaTodasAsEntidadesRegistadas() throws SQLException {
        System.setProperty("GESTAO_PENSOES_DB_URL",
                "jdbc:h2:mem:esquema_" + UUID.randomUUID().toString().replace("-", "")
                + ";DB_CLOSE_DELAY=-1");

        Set<Class<?>> entidades = HibernateUtil.getSessionFactory().getMetamodel()
                .getEntities().stream()
                .map(entidade -> entidade.getJavaType())
                .collect(java.util.stream.Collectors.toSet());
        assertTrue(entidades.containsAll(Set.of(
                Beneficiario.class, Configuracao.class, Documento.class, Historico.class,
                Pagamento.class, Pensao.class, PensaoInvalidez.class, PensaoReduzida.class,
                PensaoSobrevivencia.class, PensaoVelhice.class, PedidoReforma.class,
                Pensionista.class, Perfil.class, Permissao.class, PrestacaoMorte.class,
                ProvaVida.class, Relatorio.class, SubsidioFuneral.class, SubsidioMorte.class,
                Utilizador.class)));

        Set<String> tabelas = new HashSet<>();
        try (Session sessao = HibernateUtil.getSessionFactory().openSession()) {
            sessao.doWork(conexao -> {
                try (var resultado = conexao.getMetaData()
                        .getTables(null, null, "%", new String[]{"TABLE"})) {
                    while (resultado.next()) {
                        tabelas.add(resultado.getString("TABLE_NAME").toUpperCase());
                    }
                }
            });
        }
        assertTrue(tabelas.containsAll(Set.of(
                "BENEFICIARIOS", "CONFIGURACOES", "DOCUMENTOS", "HISTORICOS",
                "PAGAMENTOS", "PENSOES", "PEDIDOS_REFORMA", "PENSIONISTAS",
                "PERFIS", "PERMISSOES", "PRESTACOES_MORTE", "PROVAS_VIDA",
                "RELATORIOS", "UTILIZADORES")));
    }
}
