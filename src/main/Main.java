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

        while (true) {
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
            while (continuar && usuarioLogado != null) {
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

    private static boolean listarEventosDoUsuarioLogado() {
        List<Evento> eventos = objEventoDAO.listar();
        if (eventos.isEmpty()) {
            System.out.println("Nenhum evento cadastrado.");
            return false;
        } else {
            System.out.println("===== SEUS EVENTOS =====");

            for (Evento evento : eventos) {
                if (evento.getUsuario().getId() == usuarioLogado.getId()) {
                    System.out.println("ID: " + evento.getId() + " | Título: " + evento.getTitulo());
                }
            }
            return true;
        }
    }

    private static boolean processarOpcaoAdmin(int opcao) {
        switch (opcao) {
            case 1:
                System.out.println("===== MENU Gerenciar Usuários =====");
                System.out.println("1 - Listar Usuários");
                System.out.println("2 - Editar Usuário");
                System.out.println("3 - Excluir Usuário");
                System.out.println("4 - Voltar");
                System.out.print("Escolha uma opção: ");
                int opc = sc.nextInt();
                sc.nextLine();
                return gerenciarUsuarios(opc);
            case 2:
                System.out.println("===== MENU Gerenciar Eventos =====");
                System.out.println("1 - Listar Eventos");
                System.out.println("2 - Editar Evento");
                System.out.println("3 - Excluir Evento");
                System.out.println("4 - Voltar");
                System.out.println("Escolha uma opção: ");
                int opc2 = sc.nextInt();
                sc.nextLine();
                return gerenciarEventos(opc2);
            case 3:
                System.out.println("Saindo...");
                usuarioLogado = null;
                return false;
            default:
                System.out.println("Opção inválida. Tente novamente.");
        }
        return true;
    }

    private static boolean processarOpcaoComum(int opcao) {
        switch (opcao) {
            case 1:
                int opcao2 = 0;
                do {
                    System.out.println("\n=== MEUS DADOS ===");
                    System.out.println("Nome: " + usuarioLogado.getNome());
                    System.out.println("Email: " + usuarioLogado.getEmail());
                    System.out.println("Idade: " + usuarioLogado.getIdade());
                    System.out.println("CPF: " + usuarioLogado.getCpf());

                    System.out.println("\n=== MENU ===");
                    System.out.println("1 - Editar Dados");
                    System.out.println("2 - Voltar");
                    opcao2 = sc.nextInt();
                    sc.nextLine();
                    switch (opcao2) {
                        case 1: {
                            editarDadosUsuario();
                            break;
                        }
                        case 2: {
                            System.out.println("Voltando ao menu anterior...");
                            break;
                        }
                    }
                } while (opcao2 != 2);
                break;
            case 2:
                int opc = -1;
                do {
                    System.out.println(" _____________________________________________________________________________________________________________________");
                    System.out.println("|                                                   MENU DE EVENTOS                                                   |");
                    System.out.println("|_____________________________________________________________________________________________________________________|");
                    System.out.println("|          |         EVENTO        |         RECURSO         |          ATRAÇÃO           |          ENDEREÇO         |");
                    System.out.println("|INSERIR   | (1)  Inserir evento   |  (5) Inserir recurso    |   (9)  Inserir atração     |                           |");
                    System.out.println("|EDITAR    | (2)  Editar evento    |  (6) Editar recurso     |   (10) Editar atração      |   (13) Editar endereço    |");
                    System.out.println("|EXCLUIR   | (3)  Excluir evento   |  (7) Excluir recurso    |   (11) Excluir atração     |                           |");
                    System.out.println("|LISTAR    | (4)  Listar eventos   |  (8) Listar recursos    |   (12) Listar atrações     |                           |");
                    System.out.println("|_____________________________________________________________________________________________________________________|");
                    System.out.println("\n0 - Voltar");
                    System.out.print("Escolha uma opção: ");
                    opc = sc.nextInt();
                    sc.nextLine(); // limpar buffer

                    switch (opc) {
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
                            if(!listarEventosDoUsuarioLogado()){
                                break;
                            }
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
                            if(!listarEventosDoUsuarioLogado()){
                                break;
                            }
                            System.out.print("Digite o ID do evento que deseja excluir: ");
                            int id = sc.nextInt();
                            sc.nextLine();
                            objEventoDAO.excluir(id);
                            System.out.println("Evento excluído com sucesso!");
                            break;
                        }

                        case 4: {
                            if(!listarEventosDoUsuarioLogado()){
                                break;
                            }
                            List<Evento> eventos = objEventoDAO.listar();
                            boolean encontrou = false;

                            System.out.println("\n=== SEUS EVENTOS CADASTRADOS ===");

                            for (Evento evento : eventos) {
                                if (evento.getUsuario().getId() == usuarioLogado.getId()) {
                                    System.out.println("ID: " + evento.getId() +
                                            ", Título: " + evento.getTitulo() +
                                            ", Descrição: " + evento.getDescricao() +
                                            ", Tipo: " + evento.getTipo() +
                                            ", Data: " + new SimpleDateFormat("dd/MM/yyyy").format(evento.getData()) +
                                            ", Usuário: " + evento.getUsuario().getNome());
                                    encontrou = true;
                                }
                            }

                            if (!encontrou) {
                                System.out.println("Você ainda não possui eventos cadastrados.");
                            }

                            break;
                        }


                        case 5: {
                            if(!listarEventosDoUsuarioLogado()){
                                break;
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
                            break;
                        }

                        case 6: {
                            if(!listarEventosDoUsuarioLogado()){
                                break;
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
                            break;
                        }

                        case 7: {
                            if(!listarEventosDoUsuarioLogado()){
                                break;
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
                            break;
                        }

                        case 8: {
                            if(!listarEventosDoUsuarioLogado()){
                                break;
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
                            break;
                        }

                        case 9: {
                            if(!listarEventosDoUsuarioLogado()){
                                break;
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
                            break;
                        }

                        case 10: {
                            if(!listarEventosDoUsuarioLogado()){
                                break;
                            }
                            System.out.print("Digite o ID do evento para listar as atrações: ");
                            int idEvento = sc.nextInt();
                            sc.nextLine();

                            Evento evento = objEventoDAO.buscarPorId(idEvento);
                            if (evento == null) {
                                System.out.println("Evento não encontrado.");
                                break;
                            }

                            List<Atracao> atracoes = objAtracaoDAO.listar();
                            List<Atracao> atracoesEvento = atracoes.stream()
                                    .filter(a -> a.getEvento().getId() == idEvento)
                                    .toList();

                            if (atracoesEvento.isEmpty()) {
                                System.out.println("Nenhuma atração cadastrada para este evento.");
                            } else {
                                System.out.println("\n=== Atrações do Evento: " + evento.getTitulo() + " ===");
                                for (Atracao atracao : atracoesEvento) {
                                    System.out.println("ID: " + atracao.getId() + " - Nome: " + atracao.getNome());
                                }

                                System.out.print("Digite o ID da atração que deseja editar: ");
                                int idAtracao = sc.nextInt();
                                sc.nextLine();

                                Atracao atracao = objAtracaoDAO.buscarPorId(idAtracao);
                                if (atracao != null && atracao.getEvento().getId() == idEvento) {
                                    System.out.print("Novo nome (anterior: " + atracao.getNome() + "): ");
                                    atracao.setNome(sc.nextLine());

                                    System.out.print("Novo tipo (anterior: " + atracao.getTipo() + "): ");
                                    atracao.setTipo(sc.nextLine());

                                    System.out.print("Novo horário (anterior: " + atracao.getHorario() + "): ");
                                    atracao.setHorario(sc.nextLine());

                                    objAtracaoDAO.atualizar(atracao);
                                    System.out.println("Atração atualizada com sucesso!");
                                } else {
                                    System.out.println("Atração não encontrada ou não pertence a este evento.");
                                }
                            }
                            break;
                        }

                        case 11: {
                            if(!listarEventosDoUsuarioLogado()){
                                break;
                            }
                            System.out.print("Digite o ID do evento para listar as atrações: ");
                            int idEvento = sc.nextInt();
                            sc.nextLine();

                            Evento evento = objEventoDAO.buscarPorId(idEvento);
                            if (evento == null) {
                                System.out.println("Evento não encontrado.");
                                break;
                            }

                            List<Atracao> atracoes = objAtracaoDAO.listar();
                            List<Atracao> atracoesEvento = atracoes.stream()
                                    .filter(a -> a.getEvento().getId() == idEvento)
                                    .toList();

                            if (atracoesEvento.isEmpty()) {
                                System.out.println("Nenhuma atração cadastrada para este evento.");
                            } else {
                                System.out.println("\n=== Atrações do Evento: " + evento.getTitulo() + " ===");
                                for (Atracao atracao : atracoesEvento) {
                                    System.out.println("ID: " + atracao.getId() + " - Nome: " + atracao.getNome());
                                }

                                System.out.print("Digite o ID da atração que deseja excluir: ");
                                int idExcluir = sc.nextInt();
                                sc.nextLine();

                                Atracao atracao = objAtracaoDAO.buscarPorId(idExcluir);
                                if (atracao != null && atracao.getEvento().getId() == idEvento) {
                                    System.out.print("Tem certeza que deseja excluir a atração '" + atracao.getNome() + "'? (s/n): ");
                                    String confirmacao = sc.nextLine();
                                    if (confirmacao.equalsIgnoreCase("s")) {
                                        objAtracaoDAO.excluir(idExcluir);
                                        System.out.println("Atração excluída com sucesso.");
                                    } else {
                                        System.out.println("Exclusão cancelada.");
                                    }
                                } else {
                                    System.out.println("Atração não encontrada ou não pertence a este evento.");
                                }
                            }
                            break;
                        }

                        case 12: {
                            String sair = "n";
                            while (sair == "n") {
                                if(!listarEventosDoUsuarioLogado()){
                                    break;
                                }
                                System.out.print("Digite o ID do evento para listar as atrações: ");
                                int idEvento = sc.nextInt();
                                sc.nextLine();

                                Evento evento = objEventoDAO.buscarPorId(idEvento);
                                if (evento == null) {
                                    System.out.println("Evento não encontrado.");
                                    break;
                                }

                                List<Atracao> atracoes = objAtracaoDAO.listar();
                                List<Atracao> atracoesEvento = atracoes.stream()
                                        .filter(a -> a.getEvento().getId() == idEvento)
                                        .toList();

                                if (atracoesEvento.isEmpty()) {
                                    System.out.println("Nenhuma atração cadastrada para este evento.");
                                } else {
                                    System.out.println("\n=== Atrações do Evento: " + evento.getTitulo() + " ===");
                                    for (Atracao atracao : atracoesEvento) {
                                        System.out.println("ID: " + atracao.getId() +
                                                ", Nome: " + atracao.getNome() +
                                                ", Tipo: " + atracao.getTipo() +
                                                ", Horário: " + atracao.getHorario());
                                    }
                                }
                                System.out.println("Deseja listar atrações de outro evento? (s/n): ");
                                sair = sc.nextLine();
                            }
                            break;
                        }

                        case 13: {
                            if(!listarEventosDoUsuarioLogado()){
                                break;
                            }
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
                            System.out.println("Voltando ao menu anterior...");
                            break;

                        default:
                            System.out.println("Opção inválida. Tente novamente.");
                    }
                } while (opc != 0);
                break;
            case 3:
                System.out.println("Saindo...");
                usuarioLogado = null;
                return false;
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

    private static boolean listarEventos(){
        List<Evento> eventos = objEventoDAO.listar();
        if (eventos.isEmpty()) {
            System.out.println("Nenhum evento cadastrado.");
            return false;
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
            return true;
        }
    }

    private static boolean listarEnderecos(){
        List<Endereco> enderecos = objEnderecoDAO.listar();
        if (enderecos.isEmpty()) {
            System.out.println("Nenhum endereco cadastrado.");
            return false;
        } else {
            System.out.println("\n=== Lista de Enderecos ===");
            for (Endereco endereco : enderecos) {
                System.out.println("ID: " + endereco.getEstado() +
                        ", Cidade: " + endereco.getCidade() +
                        ", Rua :" + endereco.getRua() +
                        ", Número: " + endereco.getNumero() +
                        ", Loatção: " + endereco.getLotacao() +
                        ", Evento: " + endereco.getEvento().getTitulo());
            }
            return true;
        }
    }

    private static boolean listarRecursos(){
        List<Recurso> recursos = objRecursoDAO.listar();
        if (recursos.isEmpty()) {
            System.out.println("Nenhum recurso cadastrado.");
            return false;
        } else {
            System.out.println("\n=== Lista de Recursos ===");
            for (Recurso recurso : recursos) {
                System.out.println("ID: " + recurso.getId() +
                        ", Nome: " + recurso.getNome() +
                        ", Quantidade: " + recurso.getQuantidade() +
                        ", Descrição: " + recurso.getDescricao() +
                        ", Evento: " + recurso.getEvento().getTitulo());
            }
            return true;
        }
    }

    private static boolean listarAtracoes(){
        List<Atracao> atracoes = objAtracaoDAO.listar();
        if (atracoes.isEmpty()) {
            System.out.println("Nenhuma atração cadastrada.");
            return false;
        } else {
            System.out.println("\n=== Lista de Atrações ===");
            for (Atracao atraco : atracoes) {
                System.out.println("ID: " + atraco.getNome() +
                        ", Tipo: " + atraco.getTipo() +
                        ", Horário: " + atraco.getHorario() +
                        ", Evento :" + atraco.getEvento().getTitulo());
            }
            return true;
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

    private static void editarEvento() {
        if (!listarEventos()) {
            return;
        }
        System.out.print("Digite o ID do evento para continuar...");
        int id = sc.nextInt();
        sc.nextLine();

        Evento eventoExistente = objEventoDAO.buscarPorId(id);
        if (eventoExistente == null){
            System.out.println("Evento não encontrado!");
            return;
        }

        int opc = 0;

        do {
            System.out.println("1 - Atualizar eventos");
            System.out.println("2 - Atualizar endereços");
            System.out.println("3 - Atualizar recursos");
            System.out.println("4 - Atualizar atrações");
            System.out.println("5 - Voltar");

            System.out.println("Escolha uma das opções para continuar...");
            opc = sc.nextInt();
            sc.nextLine();
            switch (opc) {
                case 1:
                    System.out.print("Novo Titulo (" + eventoExistente.getTitulo() + "): ");
                    String novoTitulo = sc.nextLine();
                    if (!novoTitulo.isEmpty()) eventoExistente.setTitulo(novoTitulo);

                    System.out.print("Nova Descrição (" + eventoExistente.getDescricao() + "): ");
                    String novaDesc = sc.nextLine();
                    if (!novaDesc.isEmpty()) eventoExistente.setDescricao(novaDesc);

                    System.out.print("Novo Tipo (" + eventoExistente.getTipo() + "): ");
                    String novoTipo = sc.nextLine();
                    if (!novoTipo.isEmpty()) eventoExistente.setTipo(novoTipo);

                    System.out.print("Nova Data (dd/MM/yyyy) (" + eventoExistente.getData() + "): ");
                    try {
                        String dataStr = sc.nextLine();
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        Date data = sdf.parse(dataStr);
                        if (!dataStr.isEmpty()) eventoExistente.setData(data);
                    } catch (Exception e) {
                        System.out.println("Data inválida! Usando data atual.");
                        eventoExistente.setData(new Date());
                    }

                    objEventoDAO.atualizar(eventoExistente);
                    System.out.println("Evento atualizado com sucesso!");
                    break;
                case 2:
                    if (!listarEnderecos()){
                        return;
                    }

                    System.out.println("Digite o ID do endereço para continuar...");
                    int idEndereco = sc.nextInt();
                    sc.nextLine();

                    Endereco enderecoExistente = objEnderecoDAO.buscarPorId(idEndereco);
                    if (enderecoExistente == null) {
                        System.out.println("Endereço não encontrado!");
                        return;
                    }

                    System.out.print("Novo Estado (anterior: " + enderecoExistente.getEstado() + "): ");
                    enderecoExistente.setEstado(sc.nextLine());
                    System.out.print("Nova Cidade (anterior: " + enderecoExistente.getCidade() + "): ");
                    enderecoExistente.setCidade(sc.nextLine());
                    System.out.print("Nova Rua (anterior: " + enderecoExistente.getRua() + "): ");
                    enderecoExistente.setRua(sc.nextLine());
                    System.out.print("Novo Número (anterior: " + enderecoExistente.getNumero() + "): ");
                    enderecoExistente.setNumero(sc.nextLine());
                    System.out.print("Nova Lotação (anterior: " + enderecoExistente.getLotacao() + "): ");
                    enderecoExistente.setLotacao(sc.nextInt());
                    sc.nextLine();

                    enderecoExistente.setEvento(eventoExistente);
                    objEnderecoDAO.atualizar(enderecoExistente);
                    System.out.println("Endereço do evento atualizado com sucesso!");
                    break;
                case 3:
                    if (!listarRecursos()){
                        return;
                    }

                    System.out.println("Digite o ID do recurso para continuar...");
                    int idRecurso = sc.nextInt();
                    sc.nextLine();

                    Recurso recusoExistente = objRecursoDAO.buscarPorId(idRecurso);
                    if (recusoExistente == null) {
                        System.out.println("Recurso não encontrado!");
                        return;
                    }

                    System.out.println("Novo Nome (anterior: " + recusoExistente.getNome() + "): ");
                    recusoExistente.setNome(sc.nextLine());
                    System.out.println("Nova Quantidade (anterior: " + recusoExistente.getQuantidade());
                    recusoExistente.setQuantidade(sc.nextInt());
                    System.out.println("Nova descrição (anterior: " + recusoExistente.getDescricao());
                    recusoExistente.setDescricao(sc.nextLine());
                    sc.nextLine();

                    recusoExistente.setEvento(eventoExistente);
                    objRecursoDAO.atualizar(recusoExistente);
                    System.out.println("Recurso atualizado com sucesso!");
                    break;
                case 4:
                    if (!listarAtracoes()){
                        return;
                    }

                    System.out.println("Digite o ID do tracao para continuar...");
                    int idTracao = sc.nextInt();
                    sc.nextLine();

                    Atracao atracaoExistente = objAtracaoDAO.buscarPorId(idTracao);
                    if (atracaoExistente == null) {
                        System.out.println("Atração não encontrada!");
                        return;
                    }

                    System.out.println("Novo Nome: " + atracaoExistente.getNome());
                    atracaoExistente.setNome(sc.nextLine());
                    System.out.println("Novo Tipo: " + atracaoExistente.getTipo());
                    atracaoExistente.setTipo(sc.nextLine());
                    System.out.println("Novo Horário: " + atracaoExistente.getHorario());
                    atracaoExistente.setHorario(sc.nextLine());
                    sc.nextLine();

                    atracaoExistente.setEvento(eventoExistente);
                    objAtracaoDAO.atualizar(atracaoExistente);
                    System.out.println("Atração atualizado com sucesso!");
                    break;
                case 5:
                    System.out.println("Voltando ao menu anterior...");
                    return;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }while (opc != 5);

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

    private static void excluirEvento() {
        if (!listarEventos()) {
            return;
        }
        System.out.println("\n===== EXCLUIR EVENTO =====");
        System.out.print("Digite o ID do evento para excluir: ");
        int id = sc.nextInt();
        sc.nextLine();

        Evento eventoExistente = objEventoDAO.buscarPorId(id);
        if (eventoExistente == null){
            System.out.println("Evento não encontrado!");
            return;
        }

        objEventoDAO.excluir(id);
        System.out.println("Evento excluído com sucesso!");
    }

    private static boolean gerenciarUsuarios(int opcao) {
        switch (opcao) {
            case 1:
                listarUsuarios();
                processarOpcaoAdmin(1);
                break;
            case 2:
                editarUsuario();
                System.out.println("Deseja editar outro usuário? (s/n): ");
                if (sc.nextLine().equalsIgnoreCase("s")) {
                    gerenciarUsuarios(2);
                }else {
                    System.out.println("Voltando ao menu anterior...");
                    processarOpcaoAdmin(1);
                }
                break;
            case 3:
                excluirUsuario();
                System.out.println("Deseja excluir outro usuário? (s/n): ");
                if (sc.nextLine().equalsIgnoreCase("s")) {
                    gerenciarUsuarios(3);
                }else{
                    System.out.println("Voltando ao menu anterior...");
                    processarOpcaoAdmin(1);
                }
                break;
            case 4:
                System.out.println("Voltando ao menu anterior...");
                return false;
            default:
                System.out.println("Opção inválida. Tente novamente.");
        }
        return true;
    }

    private static boolean gerenciarEventos(int opcao) {
        switch (opcao) {
            case 1:
                listarEventos();
                processarOpcaoAdmin(2);
                break;
            case 2:
                editarEvento();
                System.out.println("Deseja editar outro evento? (s/n): ");
                if (sc.nextLine().equalsIgnoreCase("s")) {
                    gerenciarEventos(2);
                }else {
                    System.out.println("Voltando ao menu anterior...");
                    processarOpcaoAdmin(2);
                }
                break;
            case 3:
                excluirEvento();
                System.out.println("Deseja excluir outro evento? (s/n): ");
                if (sc.nextLine().equalsIgnoreCase("s")) {
                    gerenciarEventos(3);
                }else{
                    System.out.println("Voltando ao menu anterior...");
                    processarOpcaoAdmin(2);
                }
                break;
            case 4:
                System.out.println("Voltando ao menu anterior...");
                return false;
            default:
                System.out.println("Opção inválida. Tente novamente.");
        }
        return true;
    }

    public static void editarDadosUsuario() {
        int opcao;
        do {
            System.out.println("\n=== EDITAR DADOS ===");
            System.out.println("1 - Nome");
            System.out.println("2 - Idade");
            System.out.println("3 - CPF");
            System.out.println("4 - Email");
            System.out.println("5 - Senha");
            System.out.println("6 - Voltar");
            System.out.print("Escolha o que deseja editar: ");
            opcao = sc.nextInt();
            sc.nextLine();

            if (opcao >= 1 && opcao <= 4) {
                System.out.print("Digite sua senha atual para confirmar: ");
                String senha = objCriptografia.encriptarMD5(sc.nextLine());
                if (!senha.equals(usuarioLogado.getSenha())) {
                    System.out.println("Senha incorreta. Edição cancelada.");
                    continue;
                }
            }

            switch (opcao) {
                case 1:
                    System.out.print("Novo nome: ");
                    String nome = sc.nextLine();
                    System.out.print("Confirmar alteração? (s/n): ");
                    if (sc.nextLine().equalsIgnoreCase("s")) {
                        usuarioLogado.setNome(nome);
                        objUsuarioDAO.atualizar(usuarioLogado);
                        System.out.println("Nome atualizado com sucesso.");
                    } else {
                        System.out.println("Alteração cancelada.");
                    }
                    break;

                case 2:
                    System.out.print("Nova idade: ");
                    int idade = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Confirmar alteração? (s/n): ");
                    if (sc.nextLine().equalsIgnoreCase("s")) {
                        usuarioLogado.setIdade(idade);
                        objUsuarioDAO.atualizar(usuarioLogado);
                        System.out.println("Idade atualizada com sucesso.");
                    } else {
                        System.out.println("Alteração cancelada.");
                    }
                    break;

                case 3:
                    System.out.print("Novo CPF: ");
                    String cpf = sc.nextLine();
                    System.out.print("Confirmar alteração? (s/n): ");
                    if (sc.nextLine().equalsIgnoreCase("s")) {
                        usuarioLogado.setCpf(cpf);
                        objUsuarioDAO.atualizar(usuarioLogado);
                        System.out.println("CPF atualizado com sucesso.");
                    } else {
                        System.out.println("Alteração cancelada.");
                    }
                    break;

                case 4:
                    System.out.print("Novo email: ");
                    String email = sc.nextLine();
                    System.out.print("Confirmar alteração? (s/n): ");
                    if (sc.nextLine().equalsIgnoreCase("s")) {
                        usuarioLogado.setEmail(email);
                        objUsuarioDAO.atualizar(usuarioLogado);
                        System.out.println("Email atualizado com sucesso.");
                    } else {
                        System.out.println("Alteração cancelada.");
                    }
                    break;

                case 5:
                    System.out.print("Digite a senha atual: ");
                    String senhaAtual = objCriptografia.encriptarMD5(sc.nextLine());
                    if (!senhaAtual.equals(usuarioLogado.getSenha())) {
                        System.out.println("Senha atual incorreta.");
                        break;
                    }
                    System.out.print("Nova senha: ");
                    String novaSenha = objCriptografia.encriptarMD5(sc.nextLine());
                    System.out.print("Confirmar alteração? (s/n): ");
                    if (sc.nextLine().equalsIgnoreCase("s")) {
                        usuarioLogado.setSenha(novaSenha);
                        objUsuarioDAO.atualizar(usuarioLogado);
                        System.out.println("Senha atualizada com sucesso.");
                    } else {
                        System.out.println("Alteração cancelada.");
                    }
                    break;

                case 6:
                    System.out.println("Voltando ao menu anterior...");
                    break;

                default:
                    System.out.println("Opção inválida.");
            }

        } while (opcao != 6);
    }
}