package model;

import model.enums.TipoDocumento;
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

@Entity
@Table(name = "documentos")
public class Documento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TipoDocumento tipo;

    private String numero;

    @Column(name = "data_emissao")
    private LocalDate dataEmissao;

    @Column(name = "data_validade")
    private LocalDate dataValidade;

    @Column(name = "local_emissao")
    private String localEmissao;

    @Column(name = "caminho_ficheiro")
    private String caminhoFicheiro;

    @Column(name = "pessoa_id")
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
        if (o == null || getClass() != o.getClass()) return false;
        Documento that = (Documento) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}