package dao;

import model.Pensao;
import repository.RepositorioCrud;

/** Contrato de persistencia CRUD para pensoes. */
public interface PensaoDAO extends RepositorioCrud<Pensao, Long> {
}
