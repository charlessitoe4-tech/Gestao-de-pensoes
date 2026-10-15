package controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import model.Pensionista;
import model.enums.EstadoCivil;
import model.enums.Genero;
import service.PensionistaService;

public class PensionistaController {

    private final PensionistaService service;

    public PensionistaController() {
        this(new PensionistaService());
    }

    public PensionistaController(PensionistaService service) {
        this.service = Objects.requireNonNull(service, "O serviço é obrigatório.");
    }

    public Pensionista guardar(Long id, String nomeCompleto, String numeroBI, String nuit,
            String dataNascimento, String genero, String estadoCivil, String telefone,
            String email, String endereco, String provincia, String distrito,
            String numeroPensionista, String nib, String banco, String dataAposentadoria,
            boolean ativo) {
        String nome = Objects.requireNonNull(nomeCompleto, "O nome é obrigatório.").trim();
        String[] partesNome = nome.split("\\s+", 2);
        Pensionista pensionista = id == null
                ? new Pensionista()
                : service.buscarPorId(id)
                        .orElseThrow(() -> new IllegalArgumentException("Pensionista não encontrado."));
        pensionista.setNome(partesNome[0]);
        pensionista.setApelido(partesNome.length > 1 ? partesNome[1] : "");
        pensionista.setNumeroBI(valor(numeroBI));
        pensionista.setNuit(valor(nuit));
        pensionista.setDataNascimento(LocalDate.parse(
                Objects.requireNonNull(dataNascimento, "A data de nascimento é obrigatória.").trim()));
        pensionista.setGenero(Genero.valueOf(
                Objects.requireNonNull(genero, "Selecione o sexo.")));
        pensionista.setEstadoCivil(EstadoCivil.valueOf(
                Objects.requireNonNull(estadoCivil, "Selecione o estado civil.")));
        pensionista.setTelefone(valor(telefone));
        pensionista.setEmail(valor(email));
        pensionista.setEndereco(valor(endereco));
        pensionista.setProvincia(valor(provincia));
        pensionista.setDistrito(valor(distrito));
        pensionista.setNumeroPensionista(numeroPensionista == null ? null : numeroPensionista.trim());
        pensionista.setNib(valor(nib));
        pensionista.setBanco(valor(banco));
        pensionista.setDataAposentadoria(dataAposentadoria == null || dataAposentadoria.isBlank()
                ? null : LocalDate.parse(dataAposentadoria.trim()));
        pensionista.setAtivo(ativo);
        return service.guardar(pensionista);
    }

    public List<Pensionista> listar() {
        return service.listar();
    }

    public List<Pensionista> pesquisar(String criterio) {
        return service.pesquisar(criterio);
    }

    public boolean remover(Long id) {
        return service.remover(id);
    }

    private String valor(String texto) {
        return texto == null || texto.isBlank() ? null : texto.trim();
    }
}