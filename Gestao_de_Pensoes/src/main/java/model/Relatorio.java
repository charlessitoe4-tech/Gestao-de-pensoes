package model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "relatorios")
public class Relatorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String tipo;

    @Column(name = "data_geracao", nullable = false)
    private LocalDateTime dataGeracao;

    @Column(name = "utilizador_id")
    private Long utilizadorId;

    private String parametros;

    @Column(name = "caminho_ficheiro")
    private String caminhoFicheiro;

    public Relatorio() {
    }

    public Relatorio(Long id, String titulo, String tipo, LocalDateTime dataGeracao,
                     Long utilizadorId, String parametros, String caminhoFicheiro) {
        this.id = id;
        this.titulo = titulo;
        this.tipo = tipo;
        this.dataGeracao = dataGeracao;
        this.utilizadorId = utilizadorId;
        this.parametros = parametros;
        this.caminhoFicheiro = caminhoFicheiro;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public LocalDateTime getDataGeracao() { return dataGeracao; }
    public void setDataGeracao(LocalDateTime dataGeracao) { this.dataGeracao = dataGeracao; }

    public Long getUtilizadorId() { return utilizadorId; }
    public void setUtilizadorId(Long utilizadorId) { this.utilizadorId = utilizadorId; }

    public String getParametros() { return parametros; }
    public void setParametros(String parametros) { this.parametros = parametros; }

    public String getCaminhoFicheiro() { return caminhoFicheiro; }
    public void setCaminhoFicheiro(String caminhoFicheiro) { this.caminhoFicheiro = caminhoFicheiro; }
}