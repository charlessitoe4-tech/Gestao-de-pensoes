package model;

import java.time.LocalDate;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import model.enums.EstadoCivil;
import model.enums.Genero;

/**
 * Dados comuns às pessoas registadas no sistema.
 *
 * <p>É uma classe abstrata e mapeada como superclasse JPA; as classes filhas
 * fornecem as entidades concretas persistidas.</p>
 */
@MappedSuperclass
public abstract class Pessoa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    private String apelido;

    private String nuit;

    @Column(name = "numero_bi", unique = true)
    private String numeroBI;

    private LocalDate dataNascimento;

    @Enumerated(EnumType.STRING)
    private Genero genero;

    @Enumerated(EnumType.STRING)
    private EstadoCivil estadoCivil;

    private String telefone;

    private String email;

    private String endereco;

    private String provincia;

    private String distrito;

    public Pessoa() {
    }

    public Pessoa(Long id, String nome, String apelido, String nuit, String numeroBI,
                  LocalDate dataNascimento, Genero genero, EstadoCivil estadoCivil,
                  String telefone, String email, String endereco,
                  String provincia, String distrito) {
        this.id = id;
        this.nome = nome;
        this.apelido = apelido;
        this.nuit = nuit;
        this.numeroBI = numeroBI;
        this.dataNascimento = dataNascimento;
        this.genero = genero;
        this.estadoCivil = estadoCivil;
        this.telefone = telefone;
        this.email = email;
        this.endereco = endereco;
        this.provincia = provincia;
        this.distrito = distrito;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getApelido() { return apelido; }
    public void setApelido(String apelido) { this.apelido = apelido; }

    public String getNuit() { return nuit; }
    public void setNuit(String nuit) { this.nuit = nuit; }

    public String getNumeroBI() { return numeroBI; }
    public void setNumeroBI(String numeroBI) { this.numeroBI = numeroBI; }

    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }

    public Genero getGenero() { return genero; }
    public void setGenero(Genero genero) { this.genero = genero; }

    public EstadoCivil getEstadoCivil() { return estadoCivil; }
    public void setEstadoCivil(EstadoCivil estadoCivil) { this.estadoCivil = estadoCivil; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public String getProvincia() { return provincia; }
    public void setProvincia(String provincia) { this.provincia = provincia; }

    public String getDistrito() { return distrito; }
    public void setDistrito(String distrito) { this.distrito = distrito; }

    public String getNomeCompleto() {
        return (nome != null ? nome : "") + " " + (apelido != null ? apelido : "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pessoa pessoa = (Pessoa) o;
        return id != null && Objects.equals(id, pessoa.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return getNomeCompleto();
    }
}