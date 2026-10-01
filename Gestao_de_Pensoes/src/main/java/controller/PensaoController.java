package controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import model.Pensao;
import model.PensaoInvalidez;
import model.PensaoReduzida;
import model.PensaoSobrivivencia;
import model.PensaoVelhice;
import model.enums.EstadoPensao;
import model.enums.TipoPensao;
import service.PensaoService;

/** Recebe os comandos da interface e delega as operações de pensão ao serviço. */
public class PensaoController {

    private final PensaoService servico;

    public PensaoController() {
        this(new PensaoService());
    }

    public PensaoController(PensaoService servico) {
        this.servico = Objects.requireNonNull(servico, "O serviço é obrigatório.");
    }

    public Pensao guardar(Long id, String numeroProcesso, TipoPensao tipo, Long pensionistaId,
                          BigDecimal valorMensal, LocalDate dataInicio, String observacoes) {
        Pensao pensao;
        if (id == null) {
            pensao = criarPorTipo(tipo);
        } else {
            pensao = servico.buscarPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Pensão não encontrada."));
            if (pensao.getTipo() != tipo) {
                throw new IllegalArgumentException("O tipo não pode ser alterado após o registo.");
            }
        }
        pensao.setNumeroProcesso(numeroProcesso == null ? null : numeroProcesso.trim());
        pensao.setPensionistaId(pensionistaId);
        pensao.setValorMensal(valorMensal);
        pensao.setDataInicio(dataInicio);
        pensao.setObservacoes(observacoes);
        return servico.guardar(pensao);
    }

    public List<Pensao> listar() {
        return servico.listar();
    }

    public List<Pensao> pesquisar(String criterio, String tipo, String estado, Long pensionistaId) {
        return servico.pesquisar(criterio, tipo, estado, pensionistaId);
    }

    public Optional<Pensao> buscarPorId(Long id) {
        return servico.buscarPorId(id);
    }

    public boolean remover(Long id) {
        return servico.remover(id);
    }

    public Pensao aprovar(Long id) { return servico.aprovar(id); }
    public Pensao suspender(Long id) { return servico.suspender(id); }
    public Pensao reativar(Long id) { return servico.reativar(id); }
    public Pensao cancelar(Long id) { return servico.cancelar(id); }
    public Pensao arquivar(Long id) { return servico.arquivar(id); }

    public BigDecimal calcularTotalMensalAtivo() {
        return servico.calcularTotalMensalAtivo();
    }

    private Pensao criarPorTipo(TipoPensao tipo) {
        Objects.requireNonNull(tipo, "Selecione o tipo de pensão.");
        Pensao pensao = switch (tipo) {
            case VELHICE -> new PensaoVelhice();
            case INVALIDEZ -> new PensaoInvalidez();
            case SOBREVIVENCIA -> new PensaoSobrivivencia();
            case REDUZIDA -> new PensaoReduzida();
        };
        pensao.setEstado(EstadoPensao.PENDENTE);
        return pensao;
    }
}
