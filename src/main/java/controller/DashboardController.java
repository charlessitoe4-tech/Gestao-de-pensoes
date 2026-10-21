package controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import model.Beneficiario;
import model.Pagamento;
import model.Pensao;
import model.PrestacaoMorte;
import model.ProvaVida;
import model.enums.EstadoPensao;
import service.BeneficiarioService;
import service.PagamentoService;
import service.PensaoService;
import service.PrestacaoMorteService;
import service.ProvaVidaService;

/** Controlador com estatísticas e resumos para o dashboard. */
public class DashboardController {

    private final BeneficiarioService beneficiarioService;
    private final PensaoService pensaoService;
    private final PagamentoService pagamentoService;
    private final ProvaVidaService provaVidaService;
    private final PrestacaoMorteService prestacaoMorteService;

    public DashboardController() {
        this(new BeneficiarioService(),
                new PensaoService(),
                new PagamentoService(),
                new ProvaVidaService(),
                new PrestacaoMorteService());
    }

    public DashboardController(BeneficiarioService b, PensaoService p,
                               PagamentoService pag, ProvaVidaService pv,
                               PrestacaoMorteService pm) {
        this.beneficiarioService = Objects.requireNonNull(b, "Service de beneficiários é obrigatório.");
        this.pensaoService = Objects.requireNonNull(p, "Service de pensões é obrigatório.");
        this.pagamentoService = Objects.requireNonNull(pag, "Service de pagamentos é obrigatório.");
        this.provaVidaService = Objects.requireNonNull(pv, "Service de provas de vida é obrigatório.");
        this.prestacaoMorteService = Objects.requireNonNull(pm, "Service de prestações é obrigatório.");
    }

    public int contarBeneficiarios() {
        return beneficiarioService.listar().size();
    }

    public int contarPensoes() {
        return pensaoService.listar().size();
    }

    public int contarPensoesAtivas() {
        return (int) pensaoService.listar().stream()
                .filter(p -> p.getEstado() == EstadoPensao.ATIVA)
                .count();
    }

    public int contarPagamentos() {
        return pagamentoService.listar().size();
    }

    public int contarProvasVida() {
        return provaVidaService.listar().size();
    }

    public int contarPrestacoes() {
        return prestacaoMorteService.listar().size();
    }

    public BigDecimal totalPago() {
        return pagamentoService.calcularTotalPago();
    }

    public BigDecimal totalMensalAtivo() {
        return pensaoService.calcularTotalMensalAtivo();
    }

    /** Resumo geral em mapa para ser apresentado no dashboard. */
    public Map<String, Object> resumo() {
        Map<String, Object> resumo = new HashMap<>();
        resumo.put("beneficiarios", contarBeneficiarios());
        resumo.put("pensoes", contarPensoes());
        resumo.put("pensoesAtivas", contarPensoesAtivas());
        resumo.put("pagamentos", contarPagamentos());
        resumo.put("provasVida", contarProvasVida());
        resumo.put("prestacoes", contarPrestacoes());
        resumo.put("totalPago", totalPago());
        resumo.put("totalMensalAtivo", totalMensalAtivo());
        return resumo;
    }

    public List<Pensao> ultimasPensoes() {
        return pensaoService.listar();
    }

    public List<Pagamento> ultimosPagamentos() {
        return pagamentoService.listar();
    }

    public List<ProvaVida> ultimasProvasVida() {
        return provaVidaService.listar();
    }

    public List<Beneficiario> ultimosBeneficiarios() {
        return beneficiarioService.listar();
    }

    public List<PrestacaoMorte> ultimasPrestacoes() {
        return prestacaoMorteService.listar();
    }
}