package dao;

import java.util.List;
import java.util.Optional;
import model.Pensionista;

/** Contrato de acesso a dados para pensionistas. */
public interface PensionistaDAO {

    void criar(Pensionista pensionista);

    Optional<Pensionista> buscarPorId(Long id);

    Optional<Pensionista> buscarPorNumero(String numeroPensionista);

    List<Pensionista> listarTodos();

    void atualizar(Long id, Pensionista pensionista);

    boolean remover(Long id);
}