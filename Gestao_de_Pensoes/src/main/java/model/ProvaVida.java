package model;

import model.enums.EstadoProvaVida;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "provas_vida")
public class ProvaVida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pensionista_id", nullable = false)
    private Long pensionistaId;

    @Column(name = "data_realizacao", nullable = false)
    private LocalDate dataRealizacao;

    @Column(name = "data_validade")
    private LocalDate dataValidade;

    @Enumerated(EnumType.STRING)
    private EstadoProvaVida estado;

    @Column(name = "local_realizacao")
    private String localRealizacao;

    private String observacoes;

    public ProvaVida() {
    }

    public ProvaVida(Long id, Long pensionistaId, LocalDate dataRealizacao, LocalDate dataValidade,
                     EstadoProvaVida estado, String localRealizacao, String observacoes) {
        this.id = id;
        this.pensionistaId = pensionistaId;
        this.dataRealizacao = dataRealizacao;
        this.dataValidade = dataValidade;
        this.estado = estado;
        this.localRealizacao = localRealizacao;
        this.observacoes = observacoes;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPensionistaId() { return pensionistaId; }
    public void setPensionistaId(Long pensionistaId) { this.pensionistaId = pensionistaId; }

    public LocalDate getDataRealizacao() { return dataRealizacao; }
    public void setDataRealizacao(LocalDate dataRealizacao) { this.dataRealizacao = dataRealizacao; }

    public LocalDate getDataValidade() { return dataValidade; }
    public void setDataValidade(LocalDate dataValidade) { this.dataValidade = dataValidade; }

    public EstadoProvaVida getEstado() { return estado; }
    public void setEstado(EstadoProvaVida estado) { this.estado = estado; }

    public String getLocalRealizacao() { return localRealizacao; }
    public void setLocalRealizacao(String localRealizacao) { this.localRealizacao = localRealizacao; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public boolean isValida() {
        return estado == EstadoProvaVida.CONFIRMADA
                && (dataValidade == null || !dataValidade.isBefore(LocalDate.now()));
    }
}