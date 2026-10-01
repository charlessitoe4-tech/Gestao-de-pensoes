package model;

import model.enums.EstadoPensao;
import model.enums.TipoPensao;
import java.math.BigDecimal;
import java.time.LocalDate;

public class PensaoVelhice extends Pensao {

    private int anosContribuicao;
    private int idadeAposentadoria;

    public PensaoVelhice() {
        super();
        setTipo(TipoPensao.VELHICE);
    }

    public PensaoVelhice(Long id, String numeroProcesso, EstadoPensao estado,
                         BigDecimal valorMensal, LocalDate dataInicio, LocalDate dataFim,
                         Long pensionistaId, String observacoes,
                         int anosContribuicao, int idadeAposentadoria) {
        super(id, numeroProcesso, TipoPensao.VELHICE, estado, valorMensal,
                dataInicio, dataFim, pensionistaId, observacoes);
        this.anosContribuicao = anosContribuicao;
        this.idadeAposentadoria = idadeAposentadoria;
    }

    public int getAnosContribuicao() { return anosContribuicao; }
    public void setAnosContribuicao(int anosContribuicao) { this.anosContribuicao = anosContribuicao; }

    public int getIdadeAposentadoria() { return idadeAposentadoria; }
    public void setIdadeAposentadoria(int idadeAposentadoria) { this.idadeAposentadoria = idadeAposentadoria; }
}