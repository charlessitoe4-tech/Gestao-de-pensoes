package controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import model.Beneficiario;
import model.enums.EstadoCivil;
import model.enums.Genero;
import service.BeneficiarioService;

/** Liga os eventos da interface às regras de negócio dos beneficiários. */
public class BeneficiarioController {

    private final BeneficiarioService servico;

    public BeneficiarioController() {
        this(new BeneficiarioService());
    }

    public BeneficiarioController(BeneficiarioService servico) {
        this.servico = Objects.requireNonNull(servico, "O serviço é obrigatório.");
    }

    public Beneficiario guardar(Long id, String nomeCompleto, String numeroBI, String nuit,
                                String dataNascimento, String genero, String estadoCivil,
                                String telefone, String email, String endereco,
                                String nacionalidade, String provincia, String distrito) {
        String nome = Objects.requireNonNull(nomeCompleto, "O nome é obrigatório.").trim();
        String[] partesNome = nome.split("\\s+", 2);
        Beneficiario beneficiario = id == null
                ? new Beneficiario()
                : servico.buscarPorIdRecursivo(id)
                        .orElseThrow(() -> new IllegalArgumentException("Beneficiário não encontrado."));

        beneficiario.setNome(partesNome[0]);
        beneficiario.setApelido(partesNome.length > 1 ? partesNome[1] : "");
        beneficiario.setNumeroBI(numeroBI == null ? null : numeroBI.trim());
        beneficiario.setNuit(nuit == null ? null : nuit.trim());
        beneficiario.setDataNascimento(LocalDate.parse(
                Objects.requireNonNull(dataNascimento, "A data de nascimento é obrigatória.").trim()));
        beneficiario.setGenero(Genero.valueOf(
                Objects.requireNonNull(genero, "Selecione o sexo.")));
        beneficiario.setEstadoCivil(EstadoCivil.valueOf(
                Objects.requireNonNull(estadoCivil, "Selecione o estado civil.")));
        beneficiario.setTelefone(telefone == null ? null : telefone.trim());
        beneficiario.setEmail(email == null ? null : email.trim());
        beneficiario.setEndereco(endereco == null ? null : endereco.trim());
        beneficiario.setNacionalidade(nacionalidade == null ? null : nacionalidade.trim());
        beneficiario.setProvincia(provincia == null ? null : provincia.trim());
        beneficiario.setDistrito(distrito == null ? null : distrito.trim());
        return servico.guardar(beneficiario);
    }

    public List<Beneficiario> listar() {
        return servico.listar();
    }

    public List<Beneficiario> pesquisar(String criterio) {
        return servico.pesquisar(criterio);
    }

    public Optional<Beneficiario> buscarPorId(long id) {
        return servico.buscarPorIdRecursivo(id);
    }

    public boolean remover(Long id) {
        return servico.remover(id);
    }

    public Optional<Beneficiario> desfazerUltimaRemocao() {
        return servico.desfazerUltimaRemocao();
    }
}
