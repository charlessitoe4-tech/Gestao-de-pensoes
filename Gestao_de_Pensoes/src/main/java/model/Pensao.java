package model;

import model.enums.EstadoPensao;
import model.enums.TipoPensao;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

/** Base abstrata para as modalidades de pensão do domínio. */
@Entity
@Table(name = "pensoes")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "subtipo_pensao")
public abstract class Pensao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String numeroProcesso;

    @Enumerated(EnumType.STRING)
    private TipoPensao tipo;

    @Enumerated(EnumType.STRING)
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
        if (o == null || getClass() != o.getClass()) return false;
        Pensao pensao = (Pensao) o;
        return id != null && Objects.equals(id, pensao.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return numeroProcesso + " - " + tipo + " (" + estado + ")";
    }
}