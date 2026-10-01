package service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import model.Pensao;
import model.enums.EstadoPensao;
import repository.HibernatePensaoRepository;
import repository.RepositorioCrud;

/** Regras de negócio e ciclo de vida das pensões. */
public class PensaoService {

    private final RepositorioCrud<Pensao, Long> repositorio;

    public PensaoService() {
        this(new HibernatePensaoRepository());
    }

    public PensaoService(RepositorioCrud<Pensao, Long> repositorio) {
        this.repositorio = Objects.requireNonNull(repositorio, "O repositório é obrigatório.");
    }

    public Pensao guardar(Pensao pensao) {
        validarDados(pensao);
        if (pensao.getId() == null) {
            pensao.setEstado(EstadoPensao.PENDENTE);
            repositorio.criar(pensao);
        } else {
            Pensao existente = obter(pensao.getId());
            if (existente.getClass() != pensao.getClass()) {
                throw new IllegalArgumentException("Não é permitido alterar o tipo de uma pensão existente.");
            }
            if (existente.getEstado() != pensao.getEstado()) {
                throw new IllegalArgumentException("Altere o estado usando as ações do ciclo de vida.");
            }
            repositorio.atualizar(pensao.getId(), pensao);
        }
        return pensao;
    }

    public List<Pensao> listar() {
        return new ArrayList<>(repositorio.listarTodos());
    }

    public List<Pensao> pesquisar(String criterio, String tipo, String estado, Long pensionistaId) {
        String consulta = criterio == null ? "" : criterio.trim().toLowerCase(Locale.ROOT);
        List<Pensao> resultado = new ArrayList<>();
        for (Pensao pensao : listar()) {
            boolean correspondeConsulta = consulta.isEmpty()
                    || texto(pensao.getNumeroProcesso()).toLowerCase(Locale.ROOT).contains(consulta)
                    || String.valueOf(pensao.getId()).contains(consulta)
                    || String.valueOf(pensao.getPensionistaId()).contains(consulta);
            boolean correspondeTipo = tipo == null || tipo.isBlank()
                    || pensao.getTipo().name().equals(tipo);
            boolean correspondeEstado = estado == null || estado.isBlank()
                    || pensao.getEstado().name().equals(estado);
            boolean correspondePensionista = pensionistaId == null
                    || pensionistaId.equals(pensao.getPensionistaId());
            if (correspondeConsulta && correspondeTipo && correspondeEstado && correspondePensionista) {
                resultado.add(pensao);
            }
        }
        return resultado;
    }

    public Optional<Pensao> buscarPorId(Long id) {
        Objects.requireNonNull(id, "O ID é obrigatório.");
        return repositorio.buscarPorId(id);
    }

    public boolean remover(Long id) {
        Pensao pensao = obter(id);
        if (pensao.getEstado() == EstadoPensao.ATIVA) {
            throw new IllegalStateException("Suspenda ou cancele a pensão antes de a eliminar.");
        }
        return repositorio.remover(id);
    }

    public Pensao aprovar(Long id) {
        return transitar(id, EstadoPensao.ATIVA);
    }

    public Pensao suspender(Long id) {
        return transitar(id, EstadoPensao.SUSPENSA);
    }

    public Pensao reativar(Long id) {
        return transitar(id, EstadoPensao.ATIVA);
    }

    public Pensao cancelar(Long id) {
        Pensao pensao = obter(id);
        Pensao atualizada = mudarEstado(pensao, EstadoPensao.CANCELADA);
        atualizada.setDataFim(LocalDate.now());
        validarDados(atualizada);
        repositorio.atualizar(id, atualizada);
        return atualizada;
    }

    public Pensao arquivar(Long id) {
        return transitar(id, EstadoPensao.ARQUIVADA);
    }

    /** Soma os valores das pensões ativas, sem inventar regras legais de cálculo. */
    public BigDecimal calcularTotalMensalAtivo() {
        return listar().stream()
                .filter(pensao -> pensao.getEstado() == EstadoPensao.ATIVA)
                .map(Pensao::getValorMensal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Pensao transitar(Long id, EstadoPensao novoEstado) {
        Pensao pensao = obter(id);
        Pensao atualizada = mudarEstado(pensao, novoEstado);
        if (novoEstado == EstadoPensao.CANCELADA && atualizada.getDataFim() == null) {
            atualizada.setDataFim(LocalDate.now());
        }
        validarDados(atualizada);
        repositorio.atualizar(id, atualizada);
        return atualizada;
    }

    private Pensao mudarEstado(Pensao pensao, EstadoPensao novoEstado) {
        EstadoPensao atual = pensao.getEstado();
        boolean permitido = switch (atual) {
            case PENDENTE -> novoEstado == EstadoPensao.ATIVA || novoEstado == EstadoPensao.CANCELADA;
            case ATIVA -> novoEstado == EstadoPensao.SUSPENSA || novoEstado == EstadoPensao.CANCELADA;
            case SUSPENSA -> novoEstado == EstadoPensao.ATIVA || novoEstado == EstadoPensao.CANCELADA;
            case CANCELADA -> novoEstado == EstadoPensao.ARQUIVADA;
            case ARQUIVADA -> false;
        };
        if (!permitido) {
            throw new IllegalStateException(
                    "Transição de estado não permitida: " + atual + " para " + novoEstado + ".");
        }
        pensao.setEstado(novoEstado);
        return pensao;
    }

    private Pensao obter(Long id) {
        Objects.requireNonNull(id, "O ID é obrigatório.");
        return repositorio.buscarPorId(id)
                .orElseThrow(() -> new NoSuchElementException("Pensão não encontrada: " + id));
    }

    private void validarDados(Pensao pensao) {
        Objects.requireNonNull(pensao, "A pensão é obrigatória.");
        if (pensao.getNumeroProcesso() == null || pensao.getNumeroProcesso().isBlank()) {
            throw new IllegalArgumentException("O número do processo é obrigatório.");
        }
        if (pensao.getTipo() == null || pensao.getEstado() == null) {
            throw new IllegalArgumentException("O tipo e o estado da pensão são obrigatórios.");
        }
        if (pensao.getPensionistaId() == null) {
            throw new IllegalArgumentException("O pensionista é obrigatório.");
        }
        if (pensao.getValorMensal() == null || pensao.getValorMensal().signum() <= 0) {
            throw new IllegalArgumentException("O valor mensal deve ser superior a zero.");
        }
        if (pensao.getDataInicio() == null) {
            throw new IllegalArgumentException("A data de início é obrigatória.");
        }
        if (pensao.getDataFim() != null && pensao.getDataFim().isBefore(pensao.getDataInicio())) {
            throw new IllegalArgumentException("A data de fim não pode ser anterior à data de início.");
        }
    }

    private String texto(String valor) {
        return valor == null ? "" : valor;
    }
}
