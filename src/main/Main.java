package main;

import dao.AtracaoDAO;
import dao.UsuarioDAO;
import dao.EventoDAO;
import dao.RecursoDAO;
import dao.EnderecoDAO;
import model.*;
import util.Criptografia;

import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.text.SimpleDateFormat;

public class Main {
    private static Usuario usuarioLogado;
    private static UsuarioDAO objUsuarioDAO;
    private static EventoDAO objEventoDAO;
    private static RecursoDAO objRecursoDAO;
    private static AtracaoDAO objAtracaoDAO;
    private static EnderecoDAO objEnderecoDAO;
    private static Criptografia objCriptografia;
    private static Scanner sc;

    public static void main(String[] args) {
        sc = new Scanner(System.in);
        objUsuarioDAO = new UsuarioDAO();
        objEventoDAO = new EventoDAO();
        objRecursoDAO = new RecursoDAO();
        objAtracaoDAO = new AtracaoDAO();
        objEnderecoDAO = new EnderecoDAO();
        objCriptografia = new Criptografia();
        usuarioLogado = null;

        while (usuarioLogado == null) {
            System.out.println("\n===== MENU PRINCIPAL =====");
            System.out.println("1 - Login");
            System.out.println("2 - Cadastrar-se");
            System.out.println("3 - Sair");
            System.out.print("Escolha uma opção: ");
            int opcao = sc.nextInt();
            sc.nextLine(); // Consumir o ENTER

            switch (opcao) {
                case 1:
                    usuarioLogado = fazerLogin();
                    break;
                case 2:
                    fazerCadastro();
                    break;
                case 3:
                    System.out.println("Encerrando...");
                    sc.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        }

        System.out.println("\nBem-vindo, " + usuarioLogado.getNome() + "!");

        boolean continuar = true;
        while (continuar) {
            usuarioLogado.exibirMenu();
            System.out.print("Escolha uma opção: ");
            int opcao = sc.nextInt();
            sc.nextLine();

            if (usuarioLogado instanceof UsuarioAdmin) {
                continuar = processarOpcaoAdmin(opcao);
            } else if (usuarioLogado instanceof UsuarioComum) {
                continuar = processarOpcaoComum(opcao);
            } else {
                System.out.println("Tipo de usuário desconhecido.");
                continuar = false;
            }
        }

        System.out.println("Logout realizado com sucesso!\n");
    }

    private static Usuario fazerLogin() {
        System.out.println("\n===== LOGIN =====");
        System.out.print("Email: ");
        String email = sc.nextLine();
        System.out.print("Senha: ");
        String senha = objCriptografia.encriptarMD5(sc.nextLine());

        Usuario usuario = objUsuarioDAO.autenticar(email, senha);
        if (usuario == null) {
            System.out.println("Email ou senha inválidos. Tente novamente.\n");
        }
        return usuario;
    }

    private static void fazerCadastro() {
        System.out.println("\n===== CADASTRO =====");
        System.out.print("Nome: ");
        String nome = sc.nextLine();
        System.out.print("Idade: ");
        int idade = sc.nextInt();
        sc.nextLine();
        System.out.print("CPF: ");
        String cpf = sc.nextLine();
        System.out.print("Email: ");
        String email = sc.nextLine();
        System.out.print("Senha: ");
        String senha = sc.nextLine();

        UsuarioComum novoUsuario = new UsuarioComum();
        novoUsuario.setNome(nome);
        novoUsuario.setIdade(idade);
        novoUsuario.setCpf(cpf);
        novoUsuario.setEmail(email);
        novoUsuario.setSenha(objCriptografia.encriptarMD5(senha));
        novoUsuario.setTipoUsuario("comum"); // Sempre comum no cadastro

        objUsuarioDAO.inserir(novoUsuario);
        System.out.println("Cadastro realizado com sucesso!");
    }

    private static boolean processarOpcaoAdmin(int opcao) {
        switch (opcao) {
            case 1:
                System.out.println("===== MENU Gerenciar Usuários =====");
                System.out.println("1 - Listar Usuários");
                System.out.println("2 - Editar Usuário");
                System.out.println("3 - Excluir Usuário");
                System.out.println("4 - Sair");
                System.out.print("Escolha uma opção: ");
                int opc = sc.nextInt();
                sc.nextLine();
                return gerenciarUsuarios(opc);
            case 2:
                System.out.println("Gerenciando eventos...");
                break;
            case 3:
                return false;
            default:
                System.out.println("Opção inválida. Tente novamente.");
        }
        return true;
    }

    private static boolean processarOpcaoComum(int opcao) {
        switch (opcao) {
            case 1:
                System.out.println("Listando eventos disponíveis...");
                break;
            case 2:
                int opc = -1;
                do {
                    System.out.println("\n=== MENU DE EVENTOS ===");
                    System.out.println("1 - Inserir evento");
                    System.out.println("2 - Editar evento");
                    System.out.println("3 - Excluir evento");
                    System.out.println("4 - Listar eventos");
                    System.out.println("5 - Adicionar recurso");
                    System.out.println("6 - Editar recurso");
                    System.out.println("7 - Excluir recurso");
                    System.out.println("8 - Listar recursos");
                    System.out.println("9 - Adicionar atração");
                    System.out.println("10 - Editar endereço");
                    System.out.println("0 - Sair");
                    System.out.print("Escolha uma opção: ");
                    opc = sc.nextInt();
                    sc.nextLine(); // limpar buffer

                    switch (opc) {
                        case 1: {
                            Evento evento = new Evento();
                            sc.nextLine();
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

                            System.out.println("\n=== Cadastro de Endereço do Evento ===");
                            Endereco endereco = new Endereco();
                            System.out.print("Estado: ");
                            endereco.setEstado(sc.nextLine());
                            System.out.print("Cidade: ");
                            endereco.setCidade(sc.nextLine());
                            System.out.print("Rua: ");
                            endereco.setRua(sc.nextLine());
                            System.out.print("Número: ");
                            endereco.setNumero(sc.nextLine());
                            System.out.print("Lotação máxima do local: ");
                            endereco.setLotacao(sc.nextInt());
                            sc.nextLine();

                            endereco.setEvento(evento);
                            objEnderecoDAO.inserir(endereco);
                            System.out.println("Endereço do evento cadastrado com sucesso!");


                            System.out.println("\nDeseja adicionar algum recurso agora?");
                            System.out.println("1 - Sim");
                            System.out.println("2 - Não");

                            opc = sc.nextInt();
                            sc.nextLine();
                            while (opc == 1) {
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
                                opc = sc.nextInt();
                                sc.nextLine();
                            }

                            System.out.println("\nDeseja adicionar alguma atração agora?");
                            System.out.println("1 - Sim");
                            System.out.println("2 - Não");

                            opc = sc.nextInt();
                            sc.nextLine();
                            while (opc == 1) {
                                Atracao atracao = new Atracao();
                                System.out.print("Nome do atracao: ");
                                atracao.setNome(sc.nextLine());
                                System.out.print("Tipo de atracao: ");
                                atracao.setTipo(sc.nextLine());
                                System.out.print("Horário da atraçao(00:00): ");
                                atracao.setHorario(sc.nextLine());

                                atracao.setEvento(evento);
                                objAtracaoDAO.inserir(atracao);

                                System.out.println("\nAtração inserida com sucesso! Deseja inserir outra?");
                                System.out.println("1 - Sim");
                                System.out.println("2 - Não");
                                opc = sc.nextInt();
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

                        case 6: {
                            List<Recurso> recursos = objRecursoDAO.listar();
                            if (recursos.isEmpty()) {
                                System.out.println("Nenhum recurso encontrado.");
                            } else {
                                System.out.println("\n=== Lista de Recursos ===");
                                for (Recurso recurso : recursos) {
                                    System.out.println("ID: " + recurso.getId() + " - Nome: " + recurso.getNome());
                                }

                                System.out.print("\nDigite o ID do recurso que deseja editar: ");
                                int id = sc.nextInt();
                                sc.nextLine();

                                Recurso recurso = objRecursoDAO.buscarPorId(id);

                                if (recurso != null) {
                                    System.out.println("Editando o recurso: " + recurso.getNome());

                                    System.out.print("Novo nome (anterior: " + recurso.getNome() + "): ");
                                    recurso.setNome(sc.nextLine());

                                    System.out.print("Nova quantidade (anterior: " + recurso.getQuantidade() + "): ");
                                    recurso.setQuantidade(sc.nextInt());
                                    sc.nextLine();

                                    System.out.print("Nova descrição (anterior: " + recurso.getDescricao() + "): ");
                                    recurso.setDescricao(sc.nextLine());

                                    objRecursoDAO.atualizar(recurso);
                                    System.out.println("\nRecurso atualizado com sucesso!");
                                } else {
                                    System.out.println("Recurso não encontrado.");
                                }
                            }
                            break;
                        }

                        case 7: {
                            List<Recurso> recursos = objRecursoDAO.listar();

                            if (recursos.isEmpty()) {
                                System.out.println("Nenhum recurso cadastrado.");
                            } else {
                                System.out.println("\n=== Lista de Recursos ===");
                                for (Recurso recurso : recursos) {
                                    System.out.println("ID: " + recurso.getId() +
                                            ", Nome: " + recurso.getNome() +
                                            ", Evento: " + recurso.getEvento().getTitulo());
                                }

                                System.out.print("\nDigite o ID do recurso que deseja excluir: ");
                                int idExcluir = sc.nextInt();
                                sc.nextLine();

                                Recurso recurso = objRecursoDAO.buscarPorId(idExcluir);
                                if (recurso != null) {
                                    System.out.print("Tem certeza que deseja excluir o recurso '" + recurso.getNome() + "'? (s/n): ");
                                    String confirmacao = sc.nextLine();
                                    if (confirmacao.equalsIgnoreCase("s")) {
                                        objRecursoDAO.excluir(idExcluir);
                                        System.out.println("Recurso excluído com sucesso.");
                                    } else {
                                        System.out.println("Exclusão cancelada.");
                                    }
                                } else {
                                    System.out.println("Recurso não encontrado.");
                                }
                            }
                            break;
                        }

                        case 8: {
                            List<Evento> eventos = objEventoDAO.listar();

                            if (eventos.isEmpty()) {
                                System.out.println("Nenhum evento cadastrado.");
                            } else {
                                System.out.println("\n=== Lista de Eventos ===");
                                for (Evento evento : eventos) {
                                    System.out.println("ID: " + evento.getId() + ", Título: " + evento.getTitulo());
                                }

                                System.out.print("\nDigite o ID do evento para ver os recursos: ");
                                int idEvento = sc.nextInt();
                                sc.nextLine();

                                Evento evento = objEventoDAO.buscarPorId(idEvento);
                                if (evento != null) {
                                    List<Recurso> recursos = objRecursoDAO.listar();
                                    boolean encontrou = false;

                                    System.out.println("\n=== Recursos do Evento: " + evento.getTitulo() + " ===");
                                    for (Recurso recurso : recursos) {
                                        if (recurso.getEvento().getId() == evento.getId()) {
                                            encontrou = true;
                                            System.out.println("ID: " + recurso.getId() +
                                                    ", Nome: " + recurso.getNome() +
                                                    ", Quantidade: " + recurso.getQuantidade() +
                                                    ", Descrição: " + recurso.getDescricao());
                                        }
                                    }

                                    if (!encontrou) {
                                        System.out.println("Nenhum recurso associado a este evento.");
                                    }

                                } else {
                                    System.out.println("Evento não encontrado.");
                                }
                            }
                            break;
                        }

                        case 9: {
                            List<Evento> eventos = objEventoDAO.listar();
                            if (eventos.isEmpty()) {
                                System.out.println("Nenhum evento cadastrado.");
                            } else {
                                System.out.println("\n=== Lista de Eventos ===");
                                for (Evento evento : eventos) {
                                    System.out.println("ID: " + evento.getId() +
                                            ", Título: " + evento.getTitulo());
                                }

                                System.out.print("Digite o ID do evento que deseja adicionar atração: ");
                                int idEvento = sc.nextInt();
                                sc.nextLine();

                                Evento eventoSelecionado = objEventoDAO.buscarPorId(idEvento);
                                if (eventoSelecionado != null) {
                                    int opcaoAtracao = 1;
                                    while (opcaoAtracao == 1) {
                                        Atracao atracao = new Atracao();
                                        System.out.print("Nome da atracao: ");
                                        atracao.setNome(sc.nextLine());
                                        System.out.print("Tipo da atracao: ");
                                        atracao.setTipo(sc.nextLine());
                                        System.out.print("Horário da atração(00:00): ");
                                        atracao.setHorario(sc.nextLine());

                                        atracao.setEvento(eventoSelecionado);
                                        objAtracaoDAO.inserir(atracao);

                                        System.out.println("\nAtração inserida com sucesso! Deseja adicionar outra?");
                                        System.out.println("1 - Sim");
                                        System.out.println("2 - Não");
                                        opcaoAtracao = sc.nextInt();
                                        sc.nextLine();
                                    }
                                } else {
                                    System.out.println("Evento não encontrado.");
                                }
                            }
                            break;
                        }

                        case 10: {
                            System.out.print("Digite o ID do evento que deseja editar o endereço: ");
                            int id = sc.nextInt();
                            sc.nextLine();

                            Evento evento = objEventoDAO.buscarPorId(id);
                            if (evento != null) {
                                Endereco endereco = objEnderecoDAO.buscarPorEventoId(id);
                                if (endereco != null) {
                                    System.out.print("Novo estado (anterior: " + endereco.getEstado() + "): ");
                                    endereco.setEstado(sc.nextLine());
                                    System.out.print("Nova cidade (anterior: " + endereco.getCidade() + "): ");
                                    endereco.setCidade(sc.nextLine());
                                    System.out.print("Nova rua (anterior: " + endereco.getRua() + "): ");
                                    endereco.setRua(sc.nextLine());
                                    System.out.print("Novo número (anterior: " + endereco.getNumero() + "): ");
                                    endereco.setNumero(sc.nextLine());
                                    System.out.print("Nova lotação (anterior: " + endereco.getLotacao() + "): ");
                                    endereco.setLotacao(sc.nextInt());
                                    sc.nextLine();

                                    endereco.setEvento(evento); // mantém a referência ao evento
                                    objEnderecoDAO.atualizar(endereco);
                                    System.out.println("Endereço do evento atualizado com sucesso!");
                                } else {
                                    System.out.println("Endereço não encontrado para este evento.");
                                }
                            } else {
                                System.out.println("Evento não encontrado.");
                            }
                            break;
                        }

                        case 0:
                            System.out.println("Saindo...");
                            break;

                        default:
                            System.out.println("Opção inválida. Tente novamente.");
                    }
                } while (opc != 0);
                break;
            case 3:
                System.out.println("Saindo...");
                usuarioLogado = null;
            default:
                System.out.println("Opção inválida. Tente novamente.");
        }
        return true;
    }

    private static void listarUsuarios() {
        List<Usuario> usuarios = objUsuarioDAO.listar();
        System.out.println("\n===== LISTA DE USUÁRIOS =====");
        for (Usuario usuario : usuarios) {
            System.out.println("ID: " + usuario.getId() +
                    ", Nome: " + usuario.getNome() +
                    ", Email: " + usuario.getEmail() +
                    ", Tipo: " + usuario.getTipoUsuario());
        }
    }

    private static void editarUsuario() {
        System.out.println("\n===== EDITAR USUÁRIO =====");
        System.out.print("Digite o ID do usuário a editar: ");
        int id = sc.nextInt();
        sc.nextLine();

        Usuario usuarioExistente = objUsuarioDAO.buscarPorId(id);
        if (usuarioExistente == null) {
            System.out.println("Usuário não encontrado!");
            return;
        }

        System.out.print("Novo Nome (" + usuarioExistente.getNome() + "): ");
        String novoNome = sc.nextLine();
        if (!novoNome.isEmpty()) usuarioExistente.setNome(novoNome);

        System.out.print("Nova Idade (" + usuarioExistente.getIdade() + "): ");
        String novaIdadeStr = sc.nextLine();
        if (!novaIdadeStr.isEmpty()) usuarioExistente.setIdade(Integer.parseInt(novaIdadeStr));

        System.out.print("Novo Email (" + usuarioExistente.getEmail() + "): ");
        String novoEmail = sc.nextLine();
        if (!novoEmail.isEmpty()) usuarioExistente.setEmail(novoEmail);

        System.out.print("Novo Tipo (comum/admin) (" + usuarioExistente.getTipoUsuario() + "): ");
        String novoTipo = sc.nextLine();
        if (!novoTipo.isEmpty()) usuarioExistente.setTipoUsuario(novoTipo.toLowerCase());

        objUsuarioDAO.atualizar(usuarioExistente);
        System.out.println("Usuário atualizado com sucesso!");
    }

    private static void excluirUsuario() {
        System.out.println("\n===== EXCLUIR USUÁRIO =====");
        System.out.print("Digite o ID do usuário a excluir: ");
        int id = sc.nextInt();
        sc.nextLine();

        Usuario usuarioExistente = objUsuarioDAO.buscarPorId(id);
        if (usuarioExistente == null) {
            System.out.println("Usuário não encontrado!");
            return;
        }

        objUsuarioDAO.excluir(id);
        System.out.println("Usuário excluído com sucesso!");
    }

    private static boolean gerenciarUsuarios(int opcao) {
        switch (opcao) {
            case 1:
                listarUsuarios();
                break;
            case 2:
                editarUsuario();
                break;
            case 3:
                excluirUsuario();
                break;
            case 4:
                System.out.println("Saindo do menu Admin...");
                return false;
            default:
                System.out.println("Opção inválida. Tente novamente.");
        }
        return true;
    }
}