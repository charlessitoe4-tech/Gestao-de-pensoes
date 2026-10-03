package model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "configuracoes")
public class Configuracao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String chave;

    private String valor;
    private String descricao;
    private String grupo;

    public Configuracao() {
    }

    public Configuracao(Long id, String chave, String valor, String descricao, String grupo) {
        this.id = id;
        this.chave = chave;
        this.valor = valor;
        this.descricao = descricao;
        this.grupo = grupo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getChave() { return chave; }
    public void setChave(String chave) { this.chave = chave; }

    public String getValor() { return valor; }
    public void setValor(String valor) { this.valor = valor; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getGrupo() { return grupo; }
    public void setGrupo(String grupo) { this.grupo = grupo; }

    @Override
    public String toString() {
        return chave + " = " + valor;
    }
}