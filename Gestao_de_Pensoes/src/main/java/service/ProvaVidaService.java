package service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import dao.HibernateProvaVidaDAO;
import dao.ProvaVidaDAO;
import model.ProvaVida;
import model.enums.EstadoProvaVida;

/** Regras de negócio para provas de vida. */
public class ProvaVidaService {

    private final ProvaVidaDAO repositorio;

    public ProvaVidaService() {
        this(new HibernateProvaVidaDAO());
    }

    public ProvaVidaService(ProvaVidaDAO repositorio) {
        this.repositorio = Objects.requireNonNull(repositorio, "O repositório é obrigatório.");
    }

    public ProvaVida registar(ProvaVida provaVida) {
        validar(provaVida);
        if (provaVida.getId() == null) {
            repositorio.criar(provaVida);
        } else {
            repositorio.atualizar(provaVida.getId(), provaVida);
        }
        return provaVida;
    }

    public ProvaVida confirmar(Long id) {
        ProvaVida pv = obter(id);
        pv.setEstado(EstadoProvaVida.CONFIRMADA);
        if (pv.getDataValidade() == null && pv.getDataRealizacao() != null) {
            pv.setDataValidade(pv.getDataRealizacao().plusYears(1));
        }
        repositorio.atualizar(id, pv);
        return pv;
    }

    public ProvaVida marcarFalhada(Long id, String motivo) {
        ProvaVida pv = obter(id);
        pv.setEstado(EstadoProvaVida.FALHADA);
        pv.setObservacoes(motivo);
        repositorio.atualizar(id, pv);
        return pv;
    }

    public List<ProvaVida> listarPorPensionista(Long pensionistaId) {
        return repositorio.listarPorPensionista(pensionistaId);
    }

    public List<ProvaVida> listar() {
        return repositorio.listarTodos();
    }

    public Optional<ProvaVida> buscarMaisRecente(Long pensionistaId) {
        return repositorio.buscarMaisRecente(pensionistaId);
    }

    public boolean remover(Long id) {
        Objects.requireNonNull(id, "O ID é obrigatório.");
        return repositorio.remover(id);
    }

    private ProvaVida obter(Long id) {
        Objects.requireNonNull(id, "O ID é obrigatório.");
        return repositorio.buscarPorId(id)
                .orElseThrow(() -> new NoSuchElementException("Prova de vida não encontrada: " + id));
    }

    private void validar(ProvaVida pv) {
        Objects.requireNonNull(pv, "A prova de vida é obrigatória.");
        if (pv.getPensionistaId() == null) {
            throw new IllegalArgumentException("O pensionista é obrigatório.");
        }
        if (pv.getDataRealizacao() == null) {
            throw new IllegalArgumentException("A data de realização é obrigatória.");
        }
        if (pv.getDataRealizacao().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("A data de realização não pode ser futura.");
        }
        if (pv.getEstado() == null) {
            pv.setEstado(EstadoProvaVida.PENDENTE);
        }
    }

    public Optional<Object> buscarPorId(Long id) {
    }
}