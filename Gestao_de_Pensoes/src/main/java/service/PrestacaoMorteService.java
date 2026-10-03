package service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import model.PrestacaoMorte;
import model.SubsidioFuneral;
import model.SubsidioMorte;
import repository.HibernatePrestacaoMorteRepository;
import repository.RepositorioCrud;

/** Regras de negócio para prestações por morte (subsídios). */
public class PrestacaoMorteService {

    private final RepositorioCrud<PrestacaoMorte, Long> repositorio;

    public PrestacaoMorteService() {
        this(new HibernatePrestacaoMorteRepository());
    }

    public PrestacaoMorteService(RepositorioCrud<PrestacaoMorte, Long> repositorio) {
        this.repositorio = Objects.requireNonNull(repositorio, "O repositório é obrigatório.");
    }

    public PrestacaoMorte guardar(PrestacaoMorte prestacao) {
        validar(prestacao);
        if (prestacao.getId() == null) {
            prestacao.setDataSolicitacao(LocalDate.now());
            prestacao.setAprovado(false);
            repositorio.criar(prestacao);
        } else {
            repositorio.atualizar(prestacao.getId(), prestacao);
        }
        return prestacao;
    }

    public PrestacaoMorte aprovar(Long id) {
        PrestacaoMorte p = obter(id);
        p.setAprovado(true);
        p.setDataAprovacao(LocalDate.now());
        repositorio.atualizar(id, p);
        return p;
    }

    public List<PrestacaoMorte> listar() {
        return new ArrayList<>(repositorio.listarTodos());
    }

    public List<PrestacaoMorte> listarSubsidiosMorte() {
        return listar().stream().filter(p -> p instanceof SubsidioMorte).toList();
    }

    public List<PrestacaoMorte> listarSubsidiosFuneral() {
        return listar().stream().filter(p -> p instanceof SubsidioFuneral).toList();
    }

    public Optional<PrestacaoMorte> buscarPorId(Long id) {
        return repositorio.buscarPorId(id);
    }

    public boolean remover(Long id) {
        PrestacaoMorte p = obter(id);
        if (p.isAprovado()) {
            throw new IllegalStateException("Não é possível remover uma prestação já aprovada.");
        }
        return repositorio.remover(id);
    }

    private PrestacaoMorte obter(Long id) {
        Objects.requireNonNull(id, "O ID é obrigatório.");
        return repositorio.buscarPorId(id)
                .orElseThrow(() -> new NoSuchElementException("Prestação não encontrada: " + id));
    }

    private void validar(PrestacaoMorte p) {
        Objects.requireNonNull(p, "A prestação é obrigatória.");
        if (p.getNumeroProcesso() == null || p.getNumeroProcesso().isBlank()) {
            throw new IllegalArgumentException("O número do processo é obrigatório.");
        }
        if (p.getTipo() == null) {
            throw new IllegalArgumentException("O tipo é obrigatório.");
        }
        if (p.getValor() == null || p.getValor().signum() <= 0) {
            throw new IllegalArgumentException("O valor deve ser superior a zero.");
        }
        if (p.getFalecidoId() == null) {
            throw new IllegalArgumentException("O falecido é obrigatório.");
        }
        if (p.getRequerenteId() == null) {
            throw new IllegalArgumentException("O requerente é obrigatório.");
        }
    }
}