package model;

import model.enums.TipoPrestacao;
import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("SUBSIDIO_MORTE")
public class SubsidioMorte extends PrestacaoMorte {

    private String grauParentesco;
    private boolean documentacaoCompleta;

    public SubsidioMorte() {
        super();
        setTipo(TipoPrestacao.SUBSIDIO_MORTE);
    }

    public SubsidioMorte(Long id, String numeroProcesso, BigDecimal valor,
                         LocalDate dataSolicitacao, LocalDate dataAprovacao,
                         Long falecidoId, Long requerenteId, boolean aprovado,
                         String grauParentesco, boolean documentacaoCompleta) {
        super(id, numeroProcesso, TipoPrestacao.SUBSIDIO_MORTE, valor,
                dataSolicitacao, dataAprovacao, falecidoId, requerenteId, aprovado);
        this.grauParentesco = grauParentesco;
        this.documentacaoCompleta = documentacaoCompleta;
    }

    public String getGrauParentesco() { return grauParentesco; }
    public void setGrauParentesco(String grauParentesco) { this.grauParentesco = grauParentesco; }

    public boolean isDocumentacaoCompleta() { return documentacaoCompleta; }
    public void setDocumentacaoCompleta(boolean documentacaoCompleta) { this.documentacaoCompleta = documentacaoCompleta; }
}