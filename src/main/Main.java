package main;

import dao.UsuarioDAO;
import dao.EventoDAO;
import model.Usuario;
import model.Evento;
import util.Criptografia;

import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.text.SimpleDateFormat;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Usuario objUsuario = new Usuario();
        UsuarioDAO objUsuarioDAO = new UsuarioDAO();
        Criptografia objCriptografia = new Criptografia();

        // === Cadastro de Usuário ===
        System.out.println("=== Cadastro de Usuário ===");
        System.out.print("Digite seu nome: ");
        objUsuario.setNome(sc.nextLine());
        System.out.print("Digite sua idade: ");
        objUsuario.setIdade(sc.nextInt());
        sc.nextLine(); // limpar buffer
        System.out.print("Digite seu CPF: ");
        objUsuario.setCpf(sc.nextLine());
        System.out.print("Digite seu email: ");
        objUsuario.setEmail(sc.nextLine());
        System.out.print("Digite sua senha: ");
        objUsuario.setSenha(objCriptografia.encriptarMD5(sc.nextLine()));
        System.out.print("Digite o tipo de usuário: ");
        objUsuario.setTipoUsuario(sc.nextLine());

        // Inserir usuário no banco
        objUsuarioDAO.inserir(objUsuario);

        // Recuperar o último usuário inserido (assumindo que é o último da lista)
        List<Usuario> usuarios = objUsuarioDAO.listar();
        Usuario ultimoUsuario = usuarios.get(usuarios.size() - 1);

        // === Cadastro de Evento ===
        Evento objEvento = new Evento();
        EventoDAO objEventoDAO = new EventoDAO();

        System.out.println("\n=== Cadastro de Evento ===");
        System.out.print("Título do evento: ");
        objEvento.setTitulo(sc.nextLine());
        System.out.print("Descrição do evento: ");
        objEvento.setDescricao(sc.nextLine());
        System.out.print("Tipo do evento: ");
        objEvento.setTipo(sc.nextLine());
        System.out.print("Data do evento (formato: dd/MM/yyyy): ");
        try {
            String dataStr = sc.nextLine();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date data = sdf.parse(dataStr);
            objEvento.setData(data);
        } catch (Exception e) {
            System.out.println("Data inválida! Usando data atual.");
            objEvento.setData(new Date());
        }

        objEvento.setUsuario(ultimoUsuario);
        objEventoDAO.inserir(objEvento);

        System.out.println("\nEvento cadastrado com sucesso!");

        // === Listagem de Usuários ===
        System.out.println("\n=== Lista de Usuários ===");
        for (Usuario usuario : usuarios) {
            System.out.println("ID: " + usuario.getId() + ", Nome: " + usuario.getNome() + ", Idade: " + usuario.getIdade() +
                    ", CPF: " + usuario.getCpf() + ", Email: " + usuario.getEmail() + ", Tipo de Usuário: " + usuario.getTipoUsuario());
        }

        // === Listagem de Eventos ===
        System.out.println("\n=== Lista de Eventos ===");
        List<Evento> eventos = objEventoDAO.listar();
        for (Evento evento : eventos) {
            System.out.println("ID: " + evento.getId() + ", Título: " + evento.getTitulo() +
                    ", Descrição: " + evento.getDescricao() + ", Tipo: " + evento.getTipo() +
                    ", Data: " + new SimpleDateFormat("dd/MM/yyyy").format(evento.getData()) +
                    ", Usuário: " + evento.getUsuario().getNome());
        }

        sc.close();
    }
}
