package model;

import java.time.LocalDateTime;

public class Utilizador {

    private Long id;
    private String nome;
    private String username;
    private String email;
    private String senhaHash;
    private Perfil perfil;
    private boolean ativo;
    private LocalDateTime dataCriacao;
    private LocalDateTime ultimoLogin;

    public Utilizador() {
    }

    public Utilizador(Long id, String nome, String username, String email,
                      String senhaHash, Perfil perfil, boolean ativo,
                      LocalDateTime dataCriacao, LocalDateTime ultimoLogin) {
        this.id = id;
        this.nome = nome;
        this.username = username;
        this.email = email;
        this.senhaHash = senhaHash;
        this.perfil = perfil;
        this.ativo = ativo;
        this.dataCriacao = dataCriacao;
        this.ultimoLogin = ultimoLogin;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenhaHash() { return senhaHash; }
    public void setSenhaHash(String senhaHash) { this.senhaHash = senhaHash; }

    public Perfil getPerfil() { return perfil; }
    public void setPerfil(Perfil perfil) { this.perfil = perfil; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDateTime getUltimoLogin() { return ultimoLogin; }
    public void setUltimoLogin(LocalDateTime ultimoLogin) { this.ultimoLogin = ultimoLogin; }

    @Override
    public String toString() {
        return username;
    }
}