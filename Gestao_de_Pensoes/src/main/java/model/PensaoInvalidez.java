package model;

import model.enums.EstadoPensao;
import model.enums.TipoPensao;
import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("INVALIDEZ")
public class PensaoInvalidez extends Pensao {

    private String grauInvalidez;
    private String causaInvalidez;
    private String numeroLaudo;

    public PensaoInvalidez() {
        super();
        setTipo(TipoPensao.INVALIDEZ);
    }

    public PensaoInvalidez(Long id, String numeroProcesso, EstadoPensao estado,
                           BigDecimal valorMensal, LocalDate dataInicio, LocalDate dataFim,
                           Long pensionistaId, String observacoes,
                           String grauInvalidez, String causaInvalidez, String numeroLaudo) {
        super(id, numeroProcesso, TipoPensao.INVALIDEZ, estado, valorMensal,
                dataInicio, dataFim, pensionistaId, observacoes);
        this.grauInvalidez = grauInvalidez;
        this.causaInvalidez = causaInvalidez;
        this.numeroLaudo = numeroLaudo;
    }

    public String getGrauInvalidez() { return grauInvalidez; }
    public void setGrauInvalidez(String grauInvalidez) { this.grauInvalidez = grauInvalidez; }

    public String getCausaInvalidez() { return causaInvalidez; }
    public void setCausaInvalidez(String causaInvalidez) { this.causaInvalidez = causaInvalidez; }

    public String getNumeroLaudo() { return numeroLaudo; }
    public void setNumeroLaudo(String numeroLaudo) { this.numeroLaudo = numeroLaudo; }
}