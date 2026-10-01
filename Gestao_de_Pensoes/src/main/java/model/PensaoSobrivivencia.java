package model;

import model.enums.EstadoPensao;
import model.enums.TipoPensao;
import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("SOBREVIVENCIA")
public class PensaoSobrivivencia extends Pensao {

    private Long falecidoId;
    private LocalDate dataObito;
    private String numeroCertidaoObito;

    public PensaoSobrivivencia() {
        super();
        setTipo(TipoPensao.SOBREVIVENCIA);
    }

    public PensaoSobrivivencia(Long id, String numeroProcesso, EstadoPensao estado,
                               BigDecimal valorMensal, LocalDate dataInicio, LocalDate dataFim,
                               Long pensionistaId, String observacoes,
                               Long falecidoId, LocalDate dataObito, String numeroCertidaoObito) {
        super(id, numeroProcesso, TipoPensao.SOBREVIVENCIA, estado, valorMensal,
                dataInicio, dataFim, pensionistaId, observacoes);
        this.falecidoId = falecidoId;
        this.dataObito = dataObito;
        this.numeroCertidaoObito = numeroCertidaoObito;
    }

    public Long getFalecidoId() { return falecidoId; }
    public void setFalecidoId(Long falecidoId) { this.falecidoId = falecidoId; }

    public LocalDate getDataObito() { return dataObito; }
    public void setDataObito(LocalDate dataObito) { this.dataObito = dataObito; }

    public String getNumeroCertidaoObito() { return numeroCertidaoObito; }
    public void setNumeroCertidaoObito(String numeroCertidaoObito) { this.numeroCertidaoObito = numeroCertidaoObito; }
}