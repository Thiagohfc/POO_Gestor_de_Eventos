package model;

public abstract class Usuario {
    protected int id;
    protected String nome;
    protected int idade;
    protected String cpf;
    protected String email;
    protected String senha;
    private String tipoUsuario;

    public Usuario() {}

    public Usuario(int id, String nome, int idade, String cpf, String email, String senha, String tipoUsuario) {
        this.id = id;
        this.nome = nome;
        this.idade = idade;
        this.cpf = cpf;
        this.email = email;
        this.senha = senha;
        this.tipoUsuario = tipoUsuario;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public int getIdade() { return idade; }
    public void setIdade(int idade) { this.idade = idade; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public void setTipoUsuario(String tipoUsuario) { this.tipoUsuario = tipoUsuario; }

    // Método abstrato para forçar cada tipo de usuário a definir seu tipo
    public String getTipoUsuario() {
        return this.tipoUsuario;
    }

    public abstract void exibirMenu();
}
