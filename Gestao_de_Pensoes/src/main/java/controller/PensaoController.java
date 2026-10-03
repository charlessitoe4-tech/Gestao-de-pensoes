package controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.time.YearMonth;
import model.Pagamento;
import model.Pensao;
import model.PensaoInvalidez;
import model.PensaoReduzida;
import model.PensaoSobrevivencia;
import model.PensaoVelhice;
import model.enums.EstadoPensao;
import model.enums.FormaPagamento;
import model.enums.TipoPensao;
import service.PagamentoPensaoService;
import service.PensaoService;

/** Recebe os comandos da interface e delega as operações de pensão ao serviço. */
public class PensaoController {

    private final PensaoService servico;
    private final PagamentoPensaoService pagamentoService;

    public PensaoController() {
        this(new PensaoService(), new PagamentoPensaoService());
    }

    public PensaoController(PensaoService servico) {
        this(servico, new PagamentoPensaoService());
    }

    public PensaoController(PensaoService servico, PagamentoPensaoService pagamentoService) {
        this.servico = Objects.requireNonNull(servico, "O serviço é obrigatório.");
        this.pagamentoService = Objects.requireNonNull(
                pagamentoService, "O serviço de pagamentos é obrigatório.");
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

    public Pagamento registarPagamento(Long pensaoId, YearMonth periodo, FormaPagamento forma) {
        return pagamentoService.registar(pensaoId, periodo, forma);
    }

    public List<Pagamento> listarPagamentos(Long pensaoId) {
        return pagamentoService.listar(pensaoId);
    }

    private Pensao criarPorTipo(TipoPensao tipo) {
        Objects.requireNonNull(tipo, "Selecione o tipo de pensão.");
        Pensao pensao = switch (tipo) {
            case VELHICE -> new PensaoVelhice();
            case INVALIDEZ -> new PensaoInvalidez();
            case SOBREVIVENCIA -> new PensaoSobrevivencia();
            case REDUZIDA -> new PensaoReduzida();
        };
        pensao.setEstado(EstadoPensao.PENDENTE);
        return pensao;
    }
}
