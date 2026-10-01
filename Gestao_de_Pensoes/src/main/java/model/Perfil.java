package model;

import model.enums.NivelAcesso;
import java.util.ArrayList;
import java.util.List;

public class Perfil {

    private Long id;
    private String nome;
    private String descricao;
    private NivelAcesso nivelAcesso;
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