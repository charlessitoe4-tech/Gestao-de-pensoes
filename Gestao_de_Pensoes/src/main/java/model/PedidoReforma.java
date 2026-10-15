package model;

import java.time.LocalDate;
import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import model.enums.EstadoPedidoReforma;

/** Pedido de reforma submetido por um pensionista. */
@Entity
@Table(name = "pedidos_reforma")
public class PedidoReforma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_processo", nullable = false, unique = true)
    private String numeroProcesso;

    @Column(name = "pensionista_id", nullable = false)
    private Long pensionistaId;

    @Column(name = "data_solicitacao", nullable = false)
    private LocalDate dataSolicitacao;

    @Column(name = "data_decisao")
    private LocalDate dataDecisao;

    @Column(name = "anos_contribuicao", nullable = false)
    private int anosContribuicao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPedidoReforma estado;

    private String observacoes;

    @Column(name = "motivo_rejeicao")
    private String motivoRejeicao;

    public PedidoReforma() {
    }

    public PedidoReforma(Long id, String numeroProcesso, Long pensionistaId,
            LocalDate dataSolicitacao, LocalDate dataDecisao, int anosContribuicao,
            EstadoPedidoReforma estado, String observacoes, String motivoRejeicao) {
        this.id = id;
        this.numeroProcesso = numeroProcesso;
        this.pensionistaId = pensionistaId;
        this.dataSolicitacao = dataSolicitacao;
        this.dataDecisao = dataDecisao;
        this.anosContribuicao = anosContribuicao;
        this.estado = estado;
        this.observacoes = observacoes;
        this.motivoRejeicao = motivoRejeicao;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumeroProcesso() { return numeroProcesso; }
    public void setNumeroProcesso(String numeroProcesso) { this.numeroProcesso = numeroProcesso; }

    public Long getPensionistaId() { return pensionistaId; }
    public void setPensionistaId(Long pensionistaId) { this.pensionistaId = pensionistaId; }

    public LocalDate getDataSolicitacao() { return dataSolicitacao; }
    public void setDataSolicitacao(LocalDate dataSolicitacao) { this.dataSolicitacao = dataSolicitacao; }

    public LocalDate getDataDecisao() { return dataDecisao; }
    public void setDataDecisao(LocalDate dataDecisao) { this.dataDecisao = dataDecisao; }

    public int getAnosContribuicao() { return anosContribuicao; }
    public void setAnosContribuicao(int anosContribuicao) { this.anosContribuicao = anosContribuicao; }

    public EstadoPedidoReforma getEstado() { return estado; }
    public void setEstado(EstadoPedidoReforma estado) { this.estado = estado; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public String getMotivoRejeicao() { return motivoRejeicao; }
    public void setMotivoRejeicao(String motivoRejeicao) { this.motivoRejeicao = motivoRejeicao; }

    /** Submete o pedido depois de validar os dados obrigatórios. */
    public void solicitar() {
        if (estado != null) {
            throw new IllegalStateException("O pedido já foi submetido ou decidido.");
        }
        validarDados();
        dataSolicitacao = LocalDate.now();
        estado = EstadoPedidoReforma.PENDENTE;
        dataDecisao = null;
        motivoRejeicao = null;
    }

    /** Aceita um pedido pendente e regista a data da decisão. */
    public void aceitar() {
        exigirPendente();
        estado = EstadoPedidoReforma.ACEITE;
        dataDecisao = LocalDate.now();
        motivoRejeicao = null;
    }

    /** Rejeita um pedido pendente com o motivo indicado. */
    public void rejeitar(String motivo) {
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("O motivo da rejeição é obrigatório.");
        }
        exigirPendente();
        estado = EstadoPedidoReforma.REJEITADO;
        dataDecisao = LocalDate.now();
        motivoRejeicao = motivo.trim();
    }

    /** Rejeita um pedido pendente quando não é necessário indicar um motivo. */
    public void rejeitar() {
        rejeitar("Rejeitado.");
    }

    /** Indica se o pedido submetido está completo e aguarda uma decisão. */
    public boolean verificar() {
        return estado == EstadoPedidoReforma.PENDENTE
                && numeroProcesso != null && !numeroProcesso.isBlank()
                && pensionistaId != null
                && dataSolicitacao != null
                && !dataSolicitacao.isAfter(LocalDate.now())
                && anosContribuicao >= 0;
    }

    private void validarDados() {
        if (numeroProcesso == null || numeroProcesso.isBlank()) {
            throw new IllegalArgumentException("O número do processo é obrigatório.");
        }
        if (pensionistaId == null) {
            throw new IllegalArgumentException("O pensionista é obrigatório.");
        }
        if (anosContribuicao < 0) {
            throw new IllegalArgumentException("Os anos de contribuição não podem ser negativos.");
        }
    }

    private void exigirPendente() {
        if (estado != EstadoPedidoReforma.PENDENTE) {
            throw new IllegalStateException("Só é possível decidir um pedido pendente.");
        }
        if (!verificar()) {
            throw new IllegalStateException("O pedido está incompleto ou inválido.");
        }
    }

    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) {
            return true;
        }
        if (objeto == null || getClass() != objeto.getClass()) {
            return false;
        }
        PedidoReforma outro = (PedidoReforma) objeto;
        return id != null && Objects.equals(id, outro.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return numeroProcesso + " (" + estado + ")";
    }
}
