package service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import repository.HibernateRelatorioRepository;
import repository.RepositorioCrud;
import model.Relatorio;

/** Regras de negócio para relatórios. */
public class RelatorioService {

    private final RepositorioCrud<Relatorio, Long> repositorio;

    public RelatorioService() {
        this(new HibernateRelatorioRepository());
    }

    public RelatorioService(RepositorioCrud<Relatorio, Long> repositorio) {
        this.repositorio = Objects.requireNonNull(repositorio, "O repositório é obrigatório.");
    }

    public Relatorio gerar(Relatorio relatorio) {
        validar(relatorio);
        if (relatorio.getId() == null) {
            relatorio.setDataGeracao(LocalDateTime.now());
            repositorio.criar(relatorio);
        } else {
            repositorio.atualizar(relatorio.getId(), relatorio);
        }
        return relatorio;
    }

    public List<Relatorio> listar() {
        return new ArrayList<>(repositorio.listarTodos());
    }

    public Optional<Relatorio> buscarPorId(Long id) {
        Objects.requireNonNull(id, "O ID é obrigatório.");
        return repositorio.buscarPorId(id);
    }

    public List<Relatorio> listarPorTipo(String tipo) {
        Objects.requireNonNull(tipo, "O tipo é obrigatório.");
        List<Relatorio> resultado = new ArrayList<>();
        for (Relatorio r : listar()) {
            if (tipo.equalsIgnoreCase(r.getTipo())) {
                resultado.add(r);
            }
        }
        return resultado;
    }

    public boolean remover(Long id) {
        Objects.requireNonNull(id, "O ID é obrigatório.");
        return repositorio.remover(id);
    }

    private void validar(Relatorio relatorio) {
        Objects.requireNonNull(relatorio, "O relatório é obrigatório.");
        if (relatorio.getTitulo() == null || relatorio.getTitulo().isBlank()) {
            throw new IllegalArgumentException("O título é obrigatório.");
        }
        if (relatorio.getTipo() == null || relatorio.getTipo().isBlank()) {
            throw new IllegalArgumentException("O tipo é obrigatório.");
        }
    }
}