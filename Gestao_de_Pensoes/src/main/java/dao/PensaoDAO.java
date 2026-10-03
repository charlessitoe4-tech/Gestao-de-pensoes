package dao;

import java.util.List;
import java.util.Optional;
import model.Pensao;
import repository.RepositorioCrud;

/** Contrato de acesso a dados para pensões. */
public interface PensaoDAO extends RepositorioCrud<Pensao, Long> {
}
