package model;

import java.util.Scanner;

public class UsuarioAdmin extends Usuario {

    public UsuarioAdmin() {
        super();
    }

    public UsuarioAdmin(int id, String nome, int idade, String cpf, String email, String senha, String tipoUsuario) {
        super(id, nome, idade, cpf, email, senha, tipoUsuario);
    }

    @Override
    public String getTipoUsuario() {
        return "admin";
    }

    @Override
    public void exibirMenu() {
        System.out.println("===== MENU ADMIN =====");
        System.out.println("1. Gerenciar Usuários");
        System.out.println("2. Gerenciar Eventos");
        System.out.println("3. Sair");
    }
}
