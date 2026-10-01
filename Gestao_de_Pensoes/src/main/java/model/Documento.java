package model;

import model.enums.TipoDocumento;
import java.time.LocalDate;
import java.util.Objects;

public class Documento {

    private Long id;
    private TipoDocumento tipo;
    private String numero;
    private LocalDate dataEmissao;
    private LocalDate dataValidade;
    private String localEmissao;
    private String caminhoFicheiro;
    private Long pessoaId;

    public Documento() {
    }

    public Documento(Long id, TipoDocumento tipo, String numero, LocalDate dataEmissao,
                     LocalDate dataValidade, String localEmissao, String caminhoFicheiro, Long pessoaId) {
        this.id = id;
        this.tipo = tipo;
        this.numero = numero;
        this.dataEmissao = dataEmissao;
        this.dataValidade = dataValidade;
        this.localEmissao = localEmissao;
        this.caminhoFicheiro = caminhoFicheiro;
        this.pessoaId = pessoaId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public TipoDocumento getTipo() { return tipo; }
    public void setTipo(TipoDocumento tipo) { this.tipo = tipo; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public LocalDate getDataEmissao() { return dataEmissao; }
    public void setDataEmissao(LocalDate dataEmissao) { this.dataEmissao = dataEmissao; }

    public LocalDate getDataValidade() { return dataValidade; }
    public void setDataValidade(LocalDate dataValidade) { this.dataValidade = dataValidade; }

    public String getLocalEmissao() { return localEmissao; }
    public void setLocalEmissao(String localEmissao) { this.localEmissao = localEmissao; }

    public String getCaminhoFicheiro() { return caminhoFicheiro; }
    public void setCaminhoFicheiro(String caminhoFicheiro) { this.caminhoFicheiro = caminhoFicheiro; }

    public Long getPessoaId() { return pessoaId; }
    public void setPessoaId(Long pessoaId) { this.pessoaId = pessoaId; }

    public boolean isValido() {
        return dataValidade == null || !dataValidade.isBefore(LocalDate.now());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Documento)) return false;
        Documento that = (Documento) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}