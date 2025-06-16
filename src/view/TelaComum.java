package view;

import dao.UsuarioDAO;
import model.Usuario;
import model.UsuarioComum;
import util.Criptografia;
import dao.EventoDAO;
import model.Evento;
import dao.EnderecoDAO;
import model.Endereco;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JFormattedTextField;
import javax.swing.text.MaskFormatter;

public class TelaComum extends JPanel {

    private JanelaPrincipal janela;
    private UsuarioDAO usuarioDAO;
    private Criptografia criptografia;
    private EventoDAO objEventoDAO;
    private EnderecoDAO objEnderecoDAO;

    public TelaComum(JanelaPrincipal janela) {
        this.janela = janela;
        this.usuarioDAO = new UsuarioDAO();
        this.criptografia = new Criptografia();
        this.objEventoDAO = new EventoDAO();
        this.objEnderecoDAO = new EnderecoDAO();

        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel labelTitulo = new JLabel("Painel do Usuário", SwingConstants.CENTER);
        labelTitulo.setFont(new Font("SansSerif", Font.BOLD, 28));
        add(labelTitulo, BorderLayout.NORTH);

        JPanel painelBotoes = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JButton botaoMeusDados = new JButton("Meus Dados Pessoais");
        botaoMeusDados.setFont(new Font("SansSerif", Font.PLAIN, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        painelBotoes.add(botaoMeusDados, gbc);

        JButton botaoGerenciarEventos = new JButton("Gerenciar Meus Eventos");
        botaoGerenciarEventos.setFont(new Font("SansSerif", Font.PLAIN, 18));
        gbc.gridx = 0;
        gbc.gridy = 1;
        painelBotoes.add(botaoGerenciarEventos, gbc);

        JButton botaoGerenciarAtracao = new JButton("Gerenciar Atrações dos Eventos");
        botaoGerenciarAtracao.setFont(new Font("SansSerif", Font.PLAIN, 18));
        gbc.gridx = 0; gbc.gridy = 2;
        painelBotoes.add(botaoGerenciarAtracao, gbc);

        JButton botaoGerenciarRecursos = new JButton("Gerenciar Recursos dos Eventos");
        botaoGerenciarRecursos.setFont(new Font("SansSerif", Font.PLAIN, 18));
        gbc.gridx = 0; gbc.gridy = 3;
        painelBotoes.add(botaoGerenciarRecursos, gbc);

        add(painelBotoes, BorderLayout.CENTER);

        JPanel painelLogout = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton botaoLogout = new JButton("Sair (Logout)");
        painelLogout.add(botaoLogout);
        add(painelLogout, BorderLayout.SOUTH);

        botaoMeusDados.addActionListener(e -> abrirDialogoMeusDados());

        botaoGerenciarEventos.addActionListener(e -> abrirMenuEventos());

        botaoGerenciarAtracao.addActionListener(e -> {
            janela.getCardLayout().show(janela.getPainelPrincipal(), "atracoes");
        });

        botaoGerenciarRecursos.addActionListener(e -> {
            janela.getCardLayout().show(janela.getPainelPrincipal(), "recursos");
        });

        botaoLogout.addActionListener(e -> {
            janela.setUsuarioLogado(null);
            janela.setStatusBarText("Aguardando login...");
            janela.getCardLayout().show(janela.getPainelPrincipal(), "login");
        });
    }

    private void abrirMenuEventos() {
        String[] opcoes = {
                "1 - Inserir Evento",
                "2 - Editar Evento",
                "3 - Excluir Evento",
                "4 - Listar Meus Eventos",
                "0 - Voltar"
        };

        while (true) {
            String escolha = (String) JOptionPane.showInputDialog(
                    this,
                    "Escolha uma opção:",
                    "Menu de Eventos",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    opcoes,
                    opcoes[0]);

            if (escolha == null || escolha.startsWith("0")) break;

            switch (escolha.charAt(0)) {
                case '1':
                    inserirEvento();
                    break;
                case '2':
                    editarEvento();
                    break;
                case '3':
                    excluirEvento();
                    break;
                case '4':
                    listarEventos();
                    break;
                default:
                    JOptionPane.showMessageDialog(this, "Opção inválida.");
            }
        }
    }

    private void inserirEvento() {
        Usuario usuarioLogado = janela.getUsuarioLogado();
        if (usuarioLogado == null) {
            JOptionPane.showMessageDialog(this, "Usuário não está logado.");
            return;
        }

        JPanel painel = new JPanel(new GridLayout(0, 2, 10, 10));
        JTextField campoTitulo = new JTextField();
        JTextField campoDescricao = new JTextField();
        JTextField campoTipo = new JTextField();
        JTextField campoData = new JTextField("dd/MM/yyyy");

        JTextField campoEstado = new JTextField();
        JTextField campoCidade = new JTextField();
        JTextField campoRua = new JTextField();
        JTextField campoNumero = new JTextField();
        JTextField campoLotacao = new JTextField();

        painel.add(new JLabel("Título do Evento:")); painel.add(campoTitulo);
        painel.add(new JLabel("Descrição:")); painel.add(campoDescricao);
        painel.add(new JLabel("Tipo:")); painel.add(campoTipo);
        painel.add(new JLabel("Data (dd/MM/yyyy):")); painel.add(campoData);

        painel.add(new JLabel("Estado:")); painel.add(campoEstado);
        painel.add(new JLabel("Cidade:")); painel.add(campoCidade);
        painel.add(new JLabel("Rua:")); painel.add(campoRua);
        painel.add(new JLabel("Número:")); painel.add(campoNumero);
        painel.add(new JLabel("Lotação Máxima:")); painel.add(campoLotacao);

        int result = JOptionPane.showConfirmDialog(this, painel, "Inserir Novo Evento", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;

        try {
            Evento evento = new Evento();
            evento.setTitulo(campoTitulo.getText());
            evento.setDescricao(campoDescricao.getText());
            evento.setTipo(campoTipo.getText());

            Date data;
            try {
                data = new SimpleDateFormat("dd/MM/yyyy").parse(campoData.getText());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Data inválida. Usando data atual.");
                data = new Date();
            }
            evento.setData(data);
            evento.setUsuario(usuarioLogado);

            objEventoDAO.inserir(evento);

            Endereco endereco = new Endereco();
            endereco.setEstado(campoEstado.getText());
            endereco.setCidade(campoCidade.getText());
            endereco.setRua(campoRua.getText());
            endereco.setNumero(campoNumero.getText());
            endereco.setLotacao(Integer.parseInt(campoLotacao.getText()));
            endereco.setEvento(evento);
            objEnderecoDAO.inserir(endereco);

            JOptionPane.showMessageDialog(this, "Evento inserido com sucesso!");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao inserir evento: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editarEvento() {
        List<Evento> eventos = objEventoDAO.listar();
        Usuario usuarioLogado = janela.getUsuarioLogado();
        List<Evento> meusEventos = eventos.stream()
                .filter(e -> e.getUsuario().getId() == usuarioLogado.getId())
                .toList();

        if (meusEventos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Você não possui eventos cadastrados.");
            return;
        }

        String[] opcoes = meusEventos.stream()
                .map(e -> "ID: " + e.getId() + " - " + e.getTitulo())
                .toArray(String[]::new);

        String selecionado = (String) JOptionPane.showInputDialog(
                this,
                "Selecione um evento para editar:",
                "Editar Evento",
                JOptionPane.PLAIN_MESSAGE,
                null,
                opcoes,
                opcoes[0]
        );

        if (selecionado == null) return;

        int idSelecionado = Integer.parseInt(selecionado.split(":")[1].split("-")[0].trim());
        Evento evento = objEventoDAO.buscarPorId(idSelecionado);

        JPanel painel = new JPanel(new GridLayout(0, 2, 10, 10));
        JTextField campoTitulo = new JTextField(evento.getTitulo());
        JTextField campoDescricao = new JTextField(evento.getDescricao());
        JTextField campoTipo = new JTextField(evento.getTipo());
        JTextField campoData = new JTextField(new SimpleDateFormat("dd/MM/yyyy").format(evento.getData()));

        painel.add(new JLabel("Título:")); painel.add(campoTitulo);
        painel.add(new JLabel("Descrição:")); painel.add(campoDescricao);
        painel.add(new JLabel("Tipo:")); painel.add(campoTipo);
        painel.add(new JLabel("Data (dd/MM/yyyy):")); painel.add(campoData);

        int resultado = JOptionPane.showConfirmDialog(this, painel, "Editar Evento", JOptionPane.OK_CANCEL_OPTION);
        if (resultado != JOptionPane.OK_OPTION) return;

        try {
            evento.setTitulo(campoTitulo.getText());
            evento.setDescricao(campoDescricao.getText());
            evento.setTipo(campoTipo.getText());
            evento.setData(new SimpleDateFormat("dd/MM/yyyy").parse(campoData.getText()));

            objEventoDAO.atualizar(evento);
            JOptionPane.showMessageDialog(this, "Evento atualizado com sucesso!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar evento: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluirEvento() {
        List<Evento> eventos = objEventoDAO.listar();
        Usuario usuarioLogado = janela.getUsuarioLogado();
        List<Evento> meusEventos = eventos.stream()
                .filter(e -> e.getUsuario().getId() == usuarioLogado.getId())
                .collect(Collectors.toList());

        if (meusEventos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Você não possui eventos cadastrados.");
            return;
        }

        String[] opcoes = meusEventos.stream()
                .map(e -> "ID: " + e.getId() + " - " + e.getTitulo())
                .toArray(String[]::new);

        String selecionado = (String) JOptionPane.showInputDialog(
                this,
                "Selecione o evento para excluir:",
                "Excluir Evento",
                JOptionPane.PLAIN_MESSAGE,
                null,
                opcoes,
                opcoes[0]
        );

        if (selecionado == null) return;

        int idSelecionado = Integer.parseInt(selecionado.split(":")[1].split("-")[0].trim());

        int confirmacao = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja excluir este evento? Todos os dados relacionados serão removidos.",
                "Confirmação",
                JOptionPane.YES_NO_OPTION);

        if (confirmacao == JOptionPane.YES_OPTION) {
            objEventoDAO.excluir(idSelecionado);
            JOptionPane.showMessageDialog(this, "Evento excluído com sucesso.");
        }
    }

    private void listarEventos() {
        List<Evento> eventos = objEventoDAO.listar();
        Usuario usuarioLogado = janela.getUsuarioLogado();
        List<Evento> meusEventos = eventos.stream()
                .filter(e -> e.getUsuario().getId() == usuarioLogado.getId())
                .collect(Collectors.toList());

        if (meusEventos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Você não possui eventos cadastrados.");
            return;
        }

        String[] colunas = {"Título", "Descrição", "Tipo", "Data"};
        String[][] dados = new String[meusEventos.size()][colunas.length];

        for (int i = 0; i < meusEventos.size(); i++) {
            Evento e = meusEventos.get(i);
            dados[i][0] = e.getTitulo();
            dados[i][1] = e.getDescricao();
            dados[i][2] = e.getTipo();
            dados[i][3] = new SimpleDateFormat("dd/MM/yyyy").format(e.getData());
        }

        JTable tabela = new JTable(dados, colunas);
        JScrollPane scrollPane = new JScrollPane(tabela);

        JDialog dialogo = new JDialog(janela, "Meus Eventos", true);
        dialogo.add(scrollPane);
        dialogo.setSize(600, 300);
        dialogo.setLocationRelativeTo(janela);
        dialogo.setVisible(true);
    }

    private void abrirDialogoMeusDados() {
        Usuario usuarioLogado = janela.getUsuarioLogado();
        if (usuarioLogado == null) {
            JOptionPane.showMessageDialog(janela, "Erro: Não há usuário logado.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialogo = new JDialog(janela, "Meus Dados Pessoais", true);
        dialogo.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField campoNome = new JTextField(usuarioLogado.getNome(), 20);
        JTextField campoIdade = new JTextField(String.valueOf(usuarioLogado.getIdade()), 20);
        JFormattedTextField campoCpf = criarCampoCpfComMascara();
        campoCpf.setText(usuarioLogado.getCpf());

        JTextField campoEmail = new JTextField(usuarioLogado.getEmail(), 20);


        gbc.gridx = 0; gbc.gridy = 0; dialogo.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; dialogo.add(campoNome, gbc);

        gbc.gridx = 0; gbc.gridy = 1; dialogo.add(new JLabel("Idade:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; dialogo.add(campoIdade, gbc);

        gbc.gridx = 0; gbc.gridy = 2; dialogo.add(new JLabel("CPF:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; dialogo.add(campoCpf, gbc);

        gbc.gridx = 0; gbc.gridy = 3; dialogo.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; dialogo.add(campoEmail, gbc);

        JButton botaoSalvar = new JButton("Salvar Alterações");
        gbc.gridx = 1; gbc.gridy = 4;
        dialogo.add(botaoSalvar, gbc);

        botaoSalvar.addActionListener(e -> {
            try {
                usuarioLogado.setNome(campoNome.getText());
                usuarioLogado.setIdade(Integer.parseInt(campoIdade.getText()));
                usuarioLogado.setEmail(campoEmail.getText());

                String cpfSemMascara = campoCpf.getText().replaceAll("[^0-9]", "");
                if (cpfSemMascara.length() != 11) {
                    JOptionPane.showMessageDialog(dialogo, "CPF inválido. Preencha todos os 11 dígitos.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                usuarioLogado.setCpf(cpfSemMascara);

                JPasswordField campoSenhaConfirmacao = new JPasswordField();
                int result = JOptionPane.showConfirmDialog(dialogo, campoSenhaConfirmacao, "Digite sua senha para confirmar", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (result == JOptionPane.OK_OPTION) {
                    String senhaDigitada = new String(campoSenhaConfirmacao.getPassword());
                    String senhaCriptografada = criptografia.encriptarMD5(senhaDigitada);

                    if (senhaCriptografada.equals(usuarioLogado.getSenha())) {
                        usuarioDAO.atualizar(usuarioLogado);
                        JOptionPane.showMessageDialog(dialogo, "Dados atualizados com sucesso!");
                        dialogo.dispose();
                    } else {
                        JOptionPane.showMessageDialog(dialogo, "Senha incorreta!", "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialogo, "Erro ao atualizar dados: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialogo.pack();
        dialogo.setLocationRelativeTo(janela);
        dialogo.setVisible(true);
    }


    private JFormattedTextField criarCampoCpfComMascara() {
        try {
            MaskFormatter mascaraCpf = new MaskFormatter("###.###.###-##");
            mascaraCpf.setPlaceholderCharacter('_');
            return new JFormattedTextField(mascaraCpf);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao criar máscara de CPF.", "Erro", JOptionPane.ERROR_MESSAGE);
            return new JFormattedTextField();
        }
    }
}
