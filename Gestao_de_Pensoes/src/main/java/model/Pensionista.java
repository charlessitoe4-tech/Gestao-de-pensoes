package model;

import model.enums.EstadoCivil;
import model.enums.Genero;
import java.time.LocalDate;

public class Pensionista extends Pessoa {

    private String numeroPensionista;
    private String nib;
    private String banco;
    private LocalDate dataAposentadoria;
    private boolean ativo;

    public Pensionista() {
        super();
    }

    public Pensionista(Long id, String nome, String apelido, String nuit, String numeroBI,
                       LocalDate dataNascimento, Genero genero, EstadoCivil estadoCivil,
                       String telefone, String email, String endereco,
                       String provincia, String distrito,
                       String numeroPensionista, String nib, String banco,
                       LocalDate dataAposentadoria, boolean ativo) {
        super(id, nome, apelido, nuit, numeroBI, dataNascimento, genero, estadoCivil,
                telefone, email, endereco, provincia, distrito);
        this.numeroPensionista = numeroPensionista;
        this.nib = nib;
        this.banco = banco;
        this.dataAposentadoria = dataAposentadoria;
        this.ativo = ativo;
    }

    public String getNumeroPensionista() { return numeroPensionista; }
    public void setNumeroPensionista(String numeroPensionista) { this.numeroPensionista = numeroPensionista; }

    public String getNib() { return nib; }
    public void setNib(String nib) { this.nib = nib; }

    public String getBanco() { return banco; }
    public void setBanco(String banco) { this.banco = banco; }

    public LocalDate getDataAposentadoria() { return dataAposentadoria; }
    public void setDataAposentadoria(LocalDate dataAposentadoria) { this.dataAposentadoria = dataAposentadoria; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    @Override
    public String toString() {
        return getNomeCompleto() + " [" + numeroPensionista + "]";
    }
}