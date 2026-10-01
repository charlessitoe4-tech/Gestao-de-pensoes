package model;

import model.enums.TipoPrestacao;
import java.math.BigDecimal;
import java.time.LocalDate;

public class SubsidioFuneral extends PrestacaoMorte {

    private String nomeEmpresaFuneraria;
    private String numeroFatura;

    public SubsidioFuneral() {
        super();
        setTipo(TipoPrestacao.SUBSIDIO_FUNERAL);
    }

    public SubsidioFuneral(Long id, String numeroProcesso, BigDecimal valor,
                           LocalDate dataSolicitacao, LocalDate dataAprovacao,
                           Long falecidoId, Long requerenteId, boolean aprovado,
                           String nomeEmpresaFuneraria, String numeroFatura) {
        super(id, numeroProcesso, TipoPrestacao.SUBSIDIO_FUNERAL, valor,
                dataSolicitacao, dataAprovacao, falecidoId, requerenteId, aprovado);
        this.nomeEmpresaFuneraria = nomeEmpresaFuneraria;
        this.numeroFatura = numeroFatura;
    }

    public String getNomeEmpresaFuneraria() { return nomeEmpresaFuneraria; }
    public void setNomeEmpresaFuneraria(String nomeEmpresaFuneraria) { this.nomeEmpresaFuneraria = nomeEmpresaFuneraria; }

    public String getNumeroFatura() { return numeroFatura; }
    public void setNumeroFatura(String numeroFatura) { this.numeroFatura = numeroFatura; }
}