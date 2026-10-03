package model;

import model.enums.NivelAcesso;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "perfis")
public class Perfil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_acesso")
    private NivelAcesso nivelAcesso;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "perfil_permissao",
            joinColumns = @JoinColumn(name = "perfil_id"),
            inverseJoinColumns = @JoinColumn(name = "permissao_id"))
    private List<Permissao> permissoes = new ArrayList<>();

    public Perfil() {
    }

    public Perfil(Long id, String nome, String descricao, NivelAcesso nivelAcesso) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.nivelAcesso = nivelAcesso;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public NivelAcesso getNivelAcesso() { return nivelAcesso; }
    public void setNivelAcesso(NivelAcesso nivelAcesso) { this.nivelAcesso = nivelAcesso; }

    public List<Permissao> getPermissoes() { return permissoes; }
    public void setPermissoes(List<Permissao> permissoes) { this.permissoes = permissoes; }

    public void adicionarPermissao(Permissao permissao) {
        if (permissao != null && !permissoes.contains(permissao)) {
            permissoes.add(permissao);
        }
    }

    public void removerPermissao(Permissao permissao) {
        permissoes.remove(permissao);
    }

    @Override
    public String toString() {
        return nome;
    }
}