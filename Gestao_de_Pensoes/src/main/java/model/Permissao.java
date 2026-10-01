package model;

public class Permissao {

    private Long id;
    private String codigo;
    private String descricao;
    private String modulo;

    public Permissao() {
    }

    public Permissao(Long id, String codigo, String descricao, String modulo) {
        this.id = id;
        this.codigo = codigo;
        this.descricao = descricao;
        this.modulo = modulo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getModulo() { return modulo; }
    public void setModulo(String modulo) { this.modulo = modulo; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Permissao)) return false;
        Permissao that = (Permissao) o;
        return codigo != null && codigo.equals(that.codigo);
    }

    @Override
    public int hashCode() {
        return codigo != null ? codigo.hashCode() : 0;
    }

    @Override
    public String toString() {
        return codigo;
    }
}