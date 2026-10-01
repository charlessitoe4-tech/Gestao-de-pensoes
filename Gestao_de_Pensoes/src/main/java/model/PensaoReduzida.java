package model;

import model.enums.EstadoPensao;
import model.enums.TipoPensao;
import java.math.BigDecimal;
import java.time.LocalDate;

public class PensaoReduzida extends Pensao {

    private String motivoReducao;
    private BigDecimal percentualReducao;

    public PensaoReduzida() {
        super();
        setTipo(TipoPensao.REDUZIDA);
    }

    public PensaoReduzida(Long id, String numeroProcesso, EstadoPensao estado,
                          BigDecimal valorMensal, LocalDate dataInicio, LocalDate dataFim,
                          Long pensionistaId, String observacoes,
                          String motivoReducao, BigDecimal percentualReducao) {
        super(id, numeroProcesso, TipoPensao.REDUZIDA, estado, valorMensal,
                dataInicio, dataFim, pensionistaId, observacoes);
        this.motivoReducao = motivoReducao;
        this.percentualReducao = percentualReducao;
    }

    public String getMotivoReducao() { return motivoReducao; }
    public void setMotivoReducao(String motivoReducao) { this.motivoReducao = motivoReducao; }

    public BigDecimal getPercentualReducao() { return percentualReducao; }
    public void setPercentualReducao(BigDecimal percentualReducao) { this.percentualReducao = percentualReducao; }
}