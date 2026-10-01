package model;

import model.enums.TipoPrestacao;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public abstract class PrestacaoMorte {

    private Long id;
    private String numeroProcesso;
    private TipoPrestacao tipo;
    private BigDecimal valor;
    private LocalDate dataSolicitacao;
    private LocalDate dataAprovacao;
    private Long falecidoId;
    private Long requerenteId;
    private boolean aprovado;

    public PrestacaoMorte() {
    }

    public PrestacaoMorte(Long id, String numeroProcesso, TipoPrestacao tipo, BigDecimal valor,
                          LocalDate dataSolicitacao, LocalDate dataAprovacao,
                          Long falecidoId, Long requerenteId, boolean aprovado) {
        this.id = id;
        this.numeroProcesso = numeroProcesso;
        this.tipo = tipo;
        this.valor = valor;
        this.dataSolicitacao = dataSolicitacao;
        this.dataAprovacao = dataAprovacao;
        this.falecidoId = falecidoId;
        this.requerenteId = requerenteId;
        this.aprovado = aprovado;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumeroProcesso() { return numeroProcesso; }
    public void setNumeroProcesso(String numeroProcesso) { this.numeroProcesso = numeroProcesso; }

    public TipoPrestacao getTipo() { return tipo; }
    public void setTipo(TipoPrestacao tipo) { this.tipo = tipo; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public LocalDate getDataSolicitacao() { return dataSolicitacao; }
    public void setDataSolicitacao(LocalDate dataSolicitacao) { this.dataSolicitacao = dataSolicitacao; }

    public LocalDate getDataAprovacao() { return dataAprovacao; }
    public void setDataAprovacao(LocalDate dataAprovacao) { this.dataAprovacao = dataAprovacao; }

    public Long getFalecidoId() { return falecidoId; }
    public void setFalecidoId(Long falecidoId) { this.falecidoId = falecidoId; }

    public Long getRequerenteId() { return requerenteId; }
    public void setRequerenteId(Long requerenteId) { this.requerenteId = requerenteId; }

    public boolean isAprovado() { return aprovado; }
    public void setAprovado(boolean aprovado) { this.aprovado = aprovado; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PrestacaoMorte)) return false;
        PrestacaoMorte that = (PrestacaoMorte) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}