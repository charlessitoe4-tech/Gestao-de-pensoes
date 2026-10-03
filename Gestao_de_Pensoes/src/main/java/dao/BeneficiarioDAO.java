package dao;

import java.util.List;
import java.util.Optional;
import model.Beneficiario;

/** Contrato de acesso a dados para beneficiários. */
public interface BeneficiarioDAO {

    void criar(Beneficiario beneficiario);

    Optional<Beneficiario> buscarPorId(Long id);

    Optional<Beneficiario> buscarPorBI(String numeroBI);

    Optional<Beneficiario> buscarPorNuit(String nuit);

    List<Beneficiario> listarTodos();

    List<Beneficiario> listarPorPensionista(Long pensionistaId);

    void atualizar(Long id, Beneficiario beneficiario);

    boolean remover(Long id);
}