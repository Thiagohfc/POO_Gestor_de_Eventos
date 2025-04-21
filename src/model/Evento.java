package model;
import java.util.Date;
import java.util.List;

public class Evento {
    private int id;
    private String titulo;
    private String descricao;
    private String tipo;
    private Date data;
    private Usuario usuario; // FK

    private Endereco endereco;
    private List<Recurso> recurso;
  //  private List<Atracao> atracao;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public List<Recurso> getRecurso() {
        return recurso;
    }

    public void setRecurso(List<Recurso> recurso) {
        this.recurso = recurso;
    }

   // public List<Atracao> getAtracao() {
   //     return atracao;
   // }

   // public void setAtracao(List<Atracao> atracao) {
   //     this.atracao = atracao;
   // }
}
