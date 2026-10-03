package dao;

import model.Beneficiario;

/** Contrato de acesso a dados para beneficiários. */
public interface BeneficiarioDAO extends PessoaDAO<Beneficiario> {

    java.util.List<Beneficiario> listarPorPensionista(Long pensionistaId);
}