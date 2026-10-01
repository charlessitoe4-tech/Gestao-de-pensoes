package model;

import model.enums.EstadoPagamento;
import model.enums.FormaPagamento;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class Pagamento {

    private Long id;
    private Long pensaoId;
    private Long pensionistaId;
    private BigDecimal valor;
    private LocalDate dataPagamento;
    private LocalDate dataReferencia;
    private EstadoPagamento estado;
    private FormaPagamento formaPagamento;
    private String referencia;
    private String observacoes;

    public Pagamento() {
    }

    public Pagamento(Long id, Long pensaoId, Long pensionistaId, BigDecimal valor,
                     LocalDate dataPagamento, LocalDate dataReferencia,
                     EstadoPagamento estado, FormaPagamento formaPagamento,
                     String referencia, String observacoes) {
        this.id = id;
        this.pensaoId = pensaoId;
        this.pensionistaId = pensionistaId;
        this.valor = valor;
        this.dataPagamento = dataPagamento;
        this.dataReferencia = dataReferencia;
        this.estado = estado;
        this.formaPagamento = formaPagamento;
        this.referencia = referencia;
        this.observacoes = observacoes;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPensaoId() { return pensaoId; }
    public void setPensaoId(Long pensaoId) { this.pensaoId = pensaoId; }

    public Long getPensionistaId() { return pensionistaId; }
    public void setPensionistaId(Long pensionistaId) { this.pensionistaId = pensionistaId; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public LocalDate getDataPagamento() { return dataPagamento; }
    public void setDataPagamento(LocalDate dataPagamento) { this.dataPagamento = dataPagamento; }

    public LocalDate getDataReferencia() { return dataReferencia; }
    public void setDataReferencia(LocalDate dataReferencia) { this.dataReferencia = dataReferencia; }

    public EstadoPagamento getEstado() { return estado; }
    public void setEstado(EstadoPagamento estado) { this.estado = estado; }

    public FormaPagamento getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(FormaPagamento formaPagamento) { this.formaPagamento = formaPagamento; }

    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pagamento)) return false;
        Pagamento pagamento = (Pagamento) o;
        return Objects.equals(id, pagamento.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}