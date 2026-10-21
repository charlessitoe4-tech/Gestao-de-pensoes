package service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Stack;
import java.util.Vector;
import model.Beneficiario;
import repository.HibernateBeneficiarioRepository;
import repository.RepositorioCrud;

/** Regras de negócio e operações de aplicação para beneficiários. */
public class BeneficiarioService {

    private final RepositorioCrud<Beneficiario, Long> repositorio;
    private final Stack<Beneficiario> removidos = new Stack<>();
    private final Vector<String> registoOperacoes = new Vector<>();

    public BeneficiarioService() {
        this(new HibernateBeneficiarioRepository());
    }

    public BeneficiarioService(RepositorioCrud<Beneficiario, Long> repositorio) {
        this.repositorio = Objects.requireNonNull(repositorio, "O repositório é obrigatório.");
    }

    public synchronized Beneficiario guardar(Beneficiario beneficiario) {
        validar(beneficiario);
        if (beneficiario.getDataRegisto() == null) {
            beneficiario.setDataRegisto(LocalDate.now());
        }
        if (beneficiario.getId() == null) {
            repositorio.criar(beneficiario);
            registar("Criado beneficiário " + beneficiario.getNumeroBI());
        } else {
            repositorio.atualizar(beneficiario.getId(), beneficiario);
            registar("Atualizado beneficiário " + beneficiario.getNumeroBI());
        }
        return beneficiario;
    }

    public List<Beneficiario> listar() {
        return new ArrayList<>(repositorio.listarTodos());
    }

    public List<Beneficiario> pesquisar(String criterio) {
        String consulta = criterio == null ? "" : criterio.trim().toLowerCase(Locale.ROOT);
        List<Beneficiario> encontrados = new ArrayList<>();
        for (Beneficiario beneficiario : listar()) {
            String nome = beneficiario.getNomeCompleto().toLowerCase(Locale.ROOT);
            String bi = valor(beneficiario.getNumeroBI()).toLowerCase(Locale.ROOT);
            String nuit = valor(beneficiario.getNuit()).toLowerCase(Locale.ROOT);
            if (consulta.isEmpty() || nome.contains(consulta)
                    || bi.contains(consulta) || nuit.contains(consulta)) {
                encontrados.add(beneficiario);
            }
        }
        return encontrados;
    }

    public Optional<Beneficiario> buscarPorIdRecursivo(long id) {
        List<Beneficiario> beneficiarios = listar();
        beneficiarios.sort(Comparator.comparing(Beneficiario::getId));
        return PesquisaRecursiva.buscarPorId(beneficiarios, id, beneficiario -> beneficiario.getId());
    }

    public synchronized boolean remover(Long id) {
        Objects.requireNonNull(id, "O ID é obrigatório.");
        Optional<Beneficiario> existente = repositorio.buscarPorId(id);
        if (existente.isEmpty() || !repositorio.remover(id)) {
            return false;
        }
        removidos.push(existente.get());
        registar("Removido beneficiário " + existente.get().getNumeroBI());
        return true;
    }

    /** Reinsere o último beneficiário removido com um novo identificador. */
    public synchronized Optional<Beneficiario> desfazerUltimaRemocao() {
        if (removidos.empty()) {
            return Optional.empty();
        }
        Beneficiario restaurado = copiarSemId(removidos.peek());
        repositorio.criar(restaurado);
        removidos.pop();
        registar("Restaurado beneficiário " + restaurado.getNumeroBI());
        return Optional.of(restaurado);
    }

    public synchronized List<String> consultarHistorico() {
        return new ArrayList<>(registoOperacoes);
    }

    private void validar(Beneficiario beneficiario) {
        Objects.requireNonNull(beneficiario, "O beneficiário é obrigatório.");
        String[] camposObrigatorios = {
            beneficiario.getNomeCompleto(),
            beneficiario.getNumeroBI(),
            beneficiario.getTelefone()
        };
        String[] nomesCampos = {"nome", "número de BI", "telefone"};
        for (int i = 0; i < camposObrigatorios.length; i++) {
            if (camposObrigatorios[i] == null || camposObrigatorios[i].isBlank()) {
                throw new IllegalArgumentException("O campo " + nomesCampos[i] + " é obrigatório.");
            }
        }
        if (beneficiario.getDataNascimento() == null) {
            throw new IllegalArgumentException("A data de nascimento é obrigatória.");
        }
        if (beneficiario.getGenero() == null || beneficiario.getEstadoCivil() == null) {
            throw new IllegalArgumentException("Selecione o sexo e o estado civil.");
        }
    }

    private Beneficiario copiarSemId(Beneficiario original) {
        Beneficiario copia = new Beneficiario();
        copia.setNome(original.getNome());
        copia.setApelido(original.getApelido());
        copia.setNuit(original.getNuit());
        copia.setNumeroBI(original.getNumeroBI());
        copia.setDataNascimento(original.getDataNascimento());
        copia.setGenero(original.getGenero());
        copia.setEstadoCivil(original.getEstadoCivil());
        copia.setTelefone(original.getTelefone());
        copia.setEmail(original.getEmail());
        copia.setEndereco(original.getEndereco());
        copia.setProvincia(original.getProvincia());
        copia.setDistrito(original.getDistrito());
        copia.setNacionalidade(original.getNacionalidade());
        copia.setParentesco(original.getParentesco());
        copia.setDependente(original.isDependente());
        copia.setDataRegisto(original.getDataRegisto());
        copia.setPensionistaId(original.getPensionistaId());
        return copia;
    }

    private void registar(String operacao) {
        registoOperacoes.add(LocalDate.now() + " - " + operacao);
    }

    private String valor(String texto) {
        return texto == null ? "" : texto;
    }
}
