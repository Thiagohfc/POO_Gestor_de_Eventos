package main;

import dao.UsuarioDAO;
import model.Usuario;
import util.Criptografia;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Usuario objUsuario = new Usuario();
        UsuarioDAO objUsuarioDAO = new UsuarioDAO();
        Criptografia objCriptografia = new Criptografia();

        // Cadastrando um novo usuário
        System.out.println("Digite seu nome: ");
        objUsuario.setNome(sc.nextLine());
        System.out.println("Digite sua idade: ");
        objUsuario.setIdade(sc.nextInt());
        sc.nextLine();
        System.out.println("Digite seu CPF: ");
        objUsuario.setCpf(sc.nextLine());
        System.out.println("Digite seu email: ");
        objUsuario.setEmail(sc.nextLine());
        System.out.println("Digite seu senha: ");
        objUsuario.setSenha(objCriptografia.encriptarMD5(sc.nextLine()));
        System.out.println("Digite o tipo de usuario: ");
        objUsuario.setTipoUsuario(sc.nextLine());

        // Fazendo o insert dos dados coletados no scanner para o banco
        objUsuarioDAO.inserir(objUsuario);

        // Listando todos os usuários
        System.out.println("Lista de usuários:");
        for (Usuario usuario : objUsuarioDAO.listar()) {
            System.out.println("ID: " + usuario.getId() + ", Nome: " + usuario.getNome() + ", Idade: " + usuario.getIdade() +
                    ", CPF: " + usuario.getCpf() + ", Email: " + usuario.getEmail() + ", Tipo de Usuário: " + usuario.getTipoUsuario());
        }
    }
}