package model;

public class UsuarioComum extends Usuario {

    public UsuarioComum() {
        super();
        this.setTipoUsuario("comum");
    }

    public UsuarioComum(int id, String nome, int idade, String cpf, String email, String senha, String tipoUsuario) {
        super(id, nome, idade, cpf, email, senha, tipoUsuario);
    }

    @Override
    public void exibirMenu() {
        System.out.println("===== MENU do USUÁRIO =====");
        System.out.println("1. Ver meus dados");
        System.out.println("2. Ver Menu de Eventos");
        System.out.println("3. Sair");
    }
}
