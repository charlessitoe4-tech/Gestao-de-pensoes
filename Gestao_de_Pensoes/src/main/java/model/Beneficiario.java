package model;

import model.enums.EstadoCivil;
import model.enums.Genero;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/** Pessoa inscrita como beneficiária de uma prestação. */
@Entity
@Table(name = "beneficiarios")
public class Beneficiario extends Pessoa {

    private String nacionalidade;
    private String parentesco;
    private boolean dependente;
    private LocalDate dataRegisto;

    @Column(name = "pensionista_id")
    private Long pensionistaId;

    public Beneficiario() {
        super();
    }

    public Beneficiario(Long id, String nome, String apelido, String nuit, String numeroBI,
                        LocalDate dataNascimento, Genero genero, EstadoCivil estadoCivil,
                        String telefone, String email, String endereco,
                        String provincia, String distrito,
                        String parentesco, boolean dependente, LocalDate dataRegisto,
                        Long pensionistaId) {
        super(id, nome, apelido, nuit, numeroBI, dataNascimento, genero, estadoCivil,
                telefone, email, endereco, provincia, distrito);
        this.parentesco = parentesco;
        this.dependente = dependente;
        this.dataRegisto = dataRegisto;
        this.pensionistaId = pensionistaId;
    }

    public String getParentesco() { return parentesco; }
    public void setParentesco(String parentesco) { this.parentesco = parentesco; }

    public String getNacionalidade() { return nacionalidade; }
    public void setNacionalidade(String nacionalidade) { this.nacionalidade = nacionalidade; }

    public boolean isDependente() { return dependente; }
    public void setDependente(boolean dependente) { this.dependente = dependente; }

    public LocalDate getDataRegisto() { return dataRegisto; }
    public void setDataRegisto(LocalDate dataRegisto) { this.dataRegisto = dataRegisto; }

    public Long getPensionistaId() { return pensionistaId; }
    public void setPensionistaId(Long pensionistaId) { this.pensionistaId = pensionistaId; }

    @Override
    public String toString() {
        return getNomeCompleto() + " (" + parentesco + ")";
    }
}