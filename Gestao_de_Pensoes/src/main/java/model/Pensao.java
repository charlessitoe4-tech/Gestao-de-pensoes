package model;

import model.enums.EstadoPensao;
import model.enums.TipoPensao;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public abstract class Pensao {

    private Long id;
    private String numeroProcesso;
    private TipoPensao tipo;
    private EstadoPensao estado;
    private BigDecimal valorMensal;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private Long pensionistaId;
    private String observacoes;

    public Pensao() {
    }

    public Pensao(Long id, String numeroProcesso, TipoPensao tipo, EstadoPensao estado,
                  BigDecimal valorMensal, LocalDate dataInicio, LocalDate dataFim,
                  Long pensionistaId, String observacoes) {
        this.id = id;
        this.numeroProcesso = numeroProcesso;
        this.tipo = tipo;
        this.estado = estado;
        this.valorMensal = valorMensal;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.pensionistaId = pensionistaId;
        this.observacoes = observacoes;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumeroProcesso() { return numeroProcesso; }
    public void setNumeroProcesso(String numeroProcesso) { this.numeroProcesso = numeroProcesso; }

    public TipoPensao getTipo() { return tipo; }
    public void setTipo(TipoPensao tipo) { this.tipo = tipo; }

    public EstadoPensao getEstado() { return estado; }
    public void setEstado(EstadoPensao estado) { this.estado = estado; }

    public BigDecimal getValorMensal() { return valorMensal; }
    public void setValorMensal(BigDecimal valorMensal) { this.valorMensal = valorMensal; }

    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }

    public LocalDate getDataFim() { return dataFim; }
    public void setDataFim(LocalDate dataFim) { this.dataFim = dataFim; }

    public Long getPensionistaId() { return pensionistaId; }
    public void setPensionistaId(Long pensionistaId) { this.pensionistaId = pensionistaId; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public boolean isAtiva() {
        return estado == EstadoPensao.ATIVA;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pensao)) return false;
        Pensao pensao = (Pensao) o;
        return Objects.equals(id, pensao.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return numeroProcesso + " - " + tipo + " (" + estado + ")";
    }
}