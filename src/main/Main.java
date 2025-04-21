package main;

import dao.UsuarioDAO;
import dao.EventoDAO;
import dao.RecursoDAO;
import model.Recurso;
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
        EventoDAO objEventoDAO = new EventoDAO();
        RecursoDAO objRecursoDAO = new RecursoDAO();
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

        objUsuarioDAO.inserir(objUsuario);

        // Recupera o último usuário inserido
        List<Usuario> usuarios = objUsuarioDAO.listar();
        Usuario usuarioLogado = usuarios.get(usuarios.size() - 1);

        int opcao = -1;
        do {
            System.out.println("\n=== MENU DE EVENTOS ===");
            System.out.println("1 - Inserir evento");
            System.out.println("2 - Editar evento");
            System.out.println("3 - Excluir evento");
            System.out.println("4 - Listar eventos");
            System.out.println("5 - Adicionar recurso");
            System.out.println("0 - Sair");
            System.out.print("Escolha uma opção: ");
            opcao = sc.nextInt();
            sc.nextLine(); // limpar buffer

            switch (opcao) {
                case 1: {
                    Evento evento = new Evento();
                    System.out.print("Título: ");
                    evento.setTitulo(sc.nextLine());
                    System.out.print("Descrição: ");
                    evento.setDescricao(sc.nextLine());
                    System.out.print("Tipo: ");
                    evento.setTipo(sc.nextLine());
                    System.out.print("Data (dd/MM/yyyy): ");
                    try {
                        String dataStr = sc.nextLine();
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        Date data = sdf.parse(dataStr);
                        evento.setData(data);
                    } catch (Exception e) {
                        System.out.println("Data inválida! Usando data atual.");
                        evento.setData(new Date());
                    }
                    evento.setUsuario(usuarioLogado);
                    objEventoDAO.inserir(evento);
                    System.out.println("Evento inserido com sucesso!");



                    System.out.println("\nDeseja adicionar algum recurso agora?");
                    System.out.println("1 - Sim");
                    System.out.println("2 - Não");

                    opcao = sc.nextInt();
                    sc.nextLine();
                    while (opcao == 1) {
                        Recurso recurso = new Recurso();
                        System.out.print("Nome do recurso: ");
                        recurso.setNome(sc.nextLine());
                        System.out.print("Qual a quantidade necessária? ");
                        recurso.setQuantidade(sc.nextInt());
                        sc.nextLine();
                        System.out.print("Deixe uma descrição desse recurso: ");
                        recurso.setDescricao(sc.nextLine());


                        recurso.setEvento(evento);
                        objRecursoDAO.inserir(recurso);

                        System.out.println("\nRecurso inserido com sucesso! Deseja inserir outro?");
                        System.out.println("1 - Sim");
                        System.out.println("2 - Não");
                        opcao = sc.nextInt();
                        sc.nextLine();

                    }
                    break;
                }

                case 2: {
                    System.out.print("Digite o ID do evento que deseja editar: ");
                    int id = sc.nextInt();
                    sc.nextLine();

                    Evento evento = objEventoDAO.buscarPorId(id);
                    if (evento != null) {
                        System.out.print("Novo título (anterior: " + evento.getTitulo() + "): ");
                        evento.setTitulo(sc.nextLine());
                        System.out.print("Nova descrição (anterior: " + evento.getDescricao() + "): ");
                        evento.setDescricao(sc.nextLine());
                        System.out.print("Novo tipo (anterior: " + evento.getTipo() + "): ");
                        evento.setTipo(sc.nextLine());
                        System.out.print("Nova data (dd/MM/yyyy): ");
                        try {
                            String dataStr = sc.nextLine();
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                            evento.setData(sdf.parse(dataStr));
                        } catch (Exception e) {
                            System.out.println("Data inválida! Mantendo a data anterior.");
                        }
                        evento.setUsuario(usuarioLogado); // garantir consistência
                        objEventoDAO.atualizar(evento);
                        System.out.println("Evento atualizado com sucesso!");
                    } else {
                        System.out.println("Evento não encontrado.");
                    }
                    break;
                }

                case 3: {
                    System.out.print("Digite o ID do evento que deseja excluir: ");
                    int id = sc.nextInt();
                    sc.nextLine();
                    objEventoDAO.excluir(id);
                    System.out.println("Evento excluído com sucesso!");
                    break;
                }

                case 4: {
                    List<Evento> eventos = objEventoDAO.listar();
                    if (eventos.isEmpty()) {
                        System.out.println("Nenhum evento cadastrado.");
                    } else {
                        System.out.println("\n=== Lista de Eventos ===");
                        for (Evento evento : eventos) {
                            System.out.println("ID: " + evento.getId() +
                                    ", Título: " + evento.getTitulo() +
                                    ", Descrição: " + evento.getDescricao() +
                                    ", Tipo: " + evento.getTipo() +
                                    ", Data: " + new SimpleDateFormat("dd/MM/yyyy").format(evento.getData()) +
                                    ", Usuário: " + evento.getUsuario().getNome());
                        }
                    }
                    break;
                }

                case 5: {
                    List<Evento> eventos = objEventoDAO.listar();
                    if (eventos.isEmpty()) {
                        System.out.println("Nenhum evento cadastrado.");
                    } else {
                        System.out.println("\n=== Lista de Eventos ===");
                        for (Evento evento : eventos) {
                            System.out.println("ID: " + evento.getId() +
                                    ", Título: " + evento.getTitulo());
                        }

                        System.out.print("Digite o ID do evento que deseja adicionar recurso: ");
                        int idEvento = sc.nextInt();
                        sc.nextLine();

                        Evento eventoSelecionado = objEventoDAO.buscarPorId(idEvento);
                        if (eventoSelecionado != null) {
                            int opcaoRecurso = 1;
                            while (opcaoRecurso == 1) {
                                Recurso recurso = new Recurso();
                                System.out.print("Nome do recurso: ");
                                recurso.setNome(sc.nextLine());
                                System.out.print("Quantidade necessária: ");
                                recurso.setQuantidade(sc.nextInt());
                                sc.nextLine();
                                System.out.print("Descrição do recurso: ");
                                recurso.setDescricao(sc.nextLine());

                                recurso.setEvento(eventoSelecionado);
                                objRecursoDAO.inserir(recurso);

                                System.out.println("\nRecurso inserido com sucesso! Deseja adicionar outro?");
                                System.out.println("1 - Sim");
                                System.out.println("2 - Não");
                                opcaoRecurso = sc.nextInt();
                                sc.nextLine();
                            }
                        } else {
                            System.out.println("Evento não encontrado.");
                        }
                    }
                    break;
                }


                case 0:
                    System.out.println("Saindo...");
                    break;

                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        } while (opcao != 0);

        // === Listagem de Usuários ===
        System.out.println("\n=== Lista de Usuários ===");
        for (Usuario usuario : usuarios) {
            System.out.println("ID: " + usuario.getId() +
                    ", Nome: " + usuario.getNome() +
                    ", Idade: " + usuario.getIdade() +
                    ", CPF: " + usuario.getCpf() +
                    ", Email: " + usuario.getEmail() +
                    ", Tipo de Usuário: " + usuario.getTipoUsuario());
        }

        sc.close();
    }
}
