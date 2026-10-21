package service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import dao.HibernatePensionistaDAO;
import dao.PensionistaDAO;
import model.Pensionista;

/** Regras de negócio e operações de aplicação para pensionistas. */
public class PensionistaService {

    private final PensionistaDAO repositorio;

    public PensionistaService() {
        this(new HibernatePensionistaDAO());
    }

    public PensionistaService(PensionistaDAO repositorio) {
        this.repositorio = Objects.requireNonNull(repositorio, "O repositório é obrigatório.");
    }

    public Pensionista guardar(Pensionista pensionista) {
        validar(pensionista);
        if (pensionista.getId() == null) {
            repositorio.criar(pensionista);
        } else {
            repositorio.atualizar(pensionista.getId(), pensionista);
        }
        return pensionista;
    }

    public List<Pensionista> listar() {
        return new ArrayList<>(repositorio.listarTodos());
    }

    public List<Pensionista> pesquisar(String criterio) {
        String consulta = criterio == null ? "" : criterio.trim().toLowerCase(Locale.ROOT);
        List<Pensionista> encontrados = new ArrayList<>();
        for (Pensionista p : listar()) {
            String nome = p.getNomeCompleto().toLowerCase(Locale.ROOT);
            String bi = valor(p.getNumeroBI()).toLowerCase(Locale.ROOT);
            String nuit = valor(p.getNuit()).toLowerCase(Locale.ROOT);
            String numero = valor(p.getNumeroPensionista()).toLowerCase(Locale.ROOT);
            if (consulta.isEmpty() || nome.contains(consulta)
                    || bi.contains(consulta) || nuit.contains(consulta)
                    || numero.contains(consulta)) {
                encontrados.add(p);
            }
        }
        return encontrados;
    }

    public Optional<Pensionista> buscarPorId(Long id) {
        Objects.requireNonNull(id, "O ID é obrigatório.");
        return repositorio.buscarPorId(id);
    }

    public Optional<Pensionista> buscarPorNumero(String numeroPensionista) {
        return repositorio.buscarPorNumero(numeroPensionista);
    }

    public boolean remover(Long id) {
        Objects.requireNonNull(id, "O ID é obrigatório.");
        return repositorio.remover(id);
    }

    public Pensionista obter(Long id) {
        return repositorio.buscarPorId(id)
                .orElseThrow(() -> new NoSuchElementException("Pensionista não encontrado: " + id));
    }

    private void validar(Pensionista pensionista) {
        Objects.requireNonNull(pensionista, "O pensionista é obrigatório.");
        if (pensionista.getNome() == null || pensionista.getNome().isBlank()) {
            throw new IllegalArgumentException("O nome é obrigatório.");
        }
        if (pensionista.getNumeroPensionista() == null || pensionista.getNumeroPensionista().isBlank()) {
            throw new IllegalArgumentException("O número do pensionista é obrigatório.");
        }
        if (pensionista.getDataNascimento() == null) {
            throw new IllegalArgumentException("A data de nascimento é obrigatória.");
        }
        if (pensionista.getGenero() == null || pensionista.getEstadoCivil() == null) {
            throw new IllegalArgumentException("Selecione o sexo e o estado civil.");
        }
    }

    private String valor(String texto) {
        return texto == null ? "" : texto;
    }
}