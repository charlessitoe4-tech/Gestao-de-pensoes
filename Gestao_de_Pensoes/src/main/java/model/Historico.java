package model;

import java.time.LocalDateTime;

public class Historico {

    private Long id;
    private String entidade;
    private Long entidadeId;
    private String acao;
    private String descricao;
    private LocalDateTime dataHora;
    private Long utilizadorId;
    private String ipOrigem;

    public Historico() {
    }

    public Historico(Long id, String entidade, Long entidadeId, String acao,
                     String descricao, LocalDateTime dataHora,
                     Long utilizadorId, String ipOrigem) {
        this.id = id;
        this.entidade = entidade;
        this.entidadeId = entidadeId;
        this.acao = acao;
        this.descricao = descricao;
        this.dataHora = dataHora;
        this.utilizadorId = utilizadorId;
        this.ipOrigem = ipOrigem;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEntidade() { return entidade; }
    public void setEntidade(String entidade) { this.entidade = entidade; }

    public Long getEntidadeId() { return entidadeId; }
    public void setEntidadeId(Long entidadeId) { this.entidadeId = entidadeId; }

    public String getAcao() { return acao; }
    public void setAcao(String acao) { this.acao = acao; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public Long getUtilizadorId() { return utilizadorId; }
    public void setUtilizadorId(Long utilizadorId) { this.utilizadorId = utilizadorId; }

    public String getIpOrigem() { return ipOrigem; }
    public void setIpOrigem(String ipOrigem) { this.ipOrigem = ipOrigem; }
}