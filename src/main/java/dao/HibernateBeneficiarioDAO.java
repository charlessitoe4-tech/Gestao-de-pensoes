package dao;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import model.Beneficiario;
import org.hibernate.Session;
import org.hibernate.Transaction;
import persistence.HibernateUtil;

/** Implementação Hibernate do DAO de beneficiários. */
public abstract class HibernateBeneficiarioDAO extends HibernatePessoaDAO<Beneficiario>
        implements BeneficiarioDAO {

    public HibernateBeneficiarioDAO() {
        super(Beneficiario.class);
    }

    @Override
    public List<Beneficiario> listarPorPensionista(Long pensionistaId) {
        Objects.requireNonNull(pensionistaId, "O ID do pensionista é obrigatório.");
        try (Session sessao = HibernateUtil.getSessionFactory().openSession()) {
            return new ArrayList<>(sessao.createQuery(
                            "from Beneficiario b where b.pensionistaId = :pensionistaId " +
                                    "order by b.id", Beneficiario.class)
                    .setParameter("pensionistaId", pensionistaId)
                    .getResultList());
        }
    }
}