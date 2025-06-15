package view;

import dao.UsuarioDAO;
import model.Usuario;
import dao.EventoDAO;
import model.Evento;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class TelaAdmin extends JPanel {

    private JanelaPrincipal janela;
    private UsuarioDAO usuarioDAO;
    private EventoDAO eventoDAO;

    public TelaAdmin(JanelaPrincipal janela) {
        this.janela = janela;
        this.usuarioDAO = new UsuarioDAO();
        this.eventoDAO = new EventoDAO();

        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel labelTitulo = new JLabel("Painel Administrativo", SwingConstants.CENTER);
        labelTitulo.setFont(new Font("SansSerif", Font.BOLD, 28));
        add(labelTitulo, BorderLayout.NORTH);

        JPanel painelBotoes = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JButton botaoGerenciarUsuarios = new JButton("Gerenciar Usuários");
        botaoGerenciarUsuarios.setFont(new Font("SansSerif", Font.PLAIN, 18));
        gbc.gridx = 0; gbc.gridy = 0;
        painelBotoes.add(botaoGerenciarUsuarios, gbc);

        JButton botaoGerenciarEventos = new JButton("Gerenciar Todos os Eventos");
        botaoGerenciarEventos.setFont(new Font("SansSerif", Font.PLAIN, 18));
        gbc.gridx = 0; gbc.gridy = 1;
        painelBotoes.add(botaoGerenciarEventos, gbc);

        JButton botaoGerenciarRecursos = new JButton("Gerenciar Recursos dos Eventos");
        botaoGerenciarRecursos.setFont(new Font("SansSerif", Font.PLAIN, 18));
        gbc.gridx = 0; gbc.gridy = 2;
        painelBotoes.add(botaoGerenciarRecursos, gbc);

        add(painelBotoes, BorderLayout.CENTER);

        JPanel painelLogout = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton botaoLogout = new JButton("Sair (Logout)");
        painelLogout.add(botaoLogout);
        add(painelLogout, BorderLayout.SOUTH);

        botaoGerenciarUsuarios.addActionListener(e -> abrirDialogoGerenciarUsuarios());

        botaoGerenciarEventos.addActionListener(e -> abrirMenuEventosAdmin());

        botaoGerenciarRecursos.addActionListener(e -> {
            janela.getCardLayout().show(janela.getPainelPrincipal(), "recursos");
        });

        botaoLogout.addActionListener(e -> {
            janela.setUsuarioLogado(null);
            janela.setStatusBarText("Aguardando login...");
            janela.getCardLayout().show(janela.getPainelPrincipal(), "login");
        });
    }

    private void abrirDialogoGerenciarUsuarios() {
        JDialog dialogo = new JDialog(janela, "Gerenciamento de Usuários", true);
        dialogo.setSize(700, 500);
        dialogo.setLayout(new BorderLayout(10, 10));

        String[] colunas = {"ID", "Nome", "Email", "Tipo"};
        DefaultTableModel tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable tabelaUsuarios = new JTable(tableModel);
        tabelaUsuarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        atualizarTabelaUsuarios(tableModel);

        JScrollPane scrollPane = new JScrollPane(tabelaUsuarios);
        dialogo.add(scrollPane, BorderLayout.CENTER);

        JPanel painelAcoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton botaoEditar = new JButton("Editar Selecionado");
        JButton botaoExcluir = new JButton("Excluir Selecionado");
        JButton botaoAtualizar = new JButton("Atualizar Lista");
        painelAcoes.add(botaoEditar);
        painelAcoes.add(botaoExcluir);
        painelAcoes.add(botaoAtualizar);
        dialogo.add(painelAcoes, BorderLayout.SOUTH);

        botaoAtualizar.addActionListener(e -> atualizarTabelaUsuarios(tableModel));

        botaoExcluir.addActionListener(e -> {
            int selectedRow = tabelaUsuarios.getSelectedRow();
            if (selectedRow >= 0) {
                int idUsuario = (int) tableModel.getValueAt(selectedRow, 0);
                String nomeUsuario = (String) tableModel.getValueAt(selectedRow, 1);

                int confirm = JOptionPane.showConfirmDialog(dialogo,
                        "Tem certeza que deseja excluir o usuário '" + nomeUsuario + "' (ID: " + idUsuario + ")?",
                        "Confirmar Exclusão",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    usuarioDAO.excluir(idUsuario);
                    atualizarTabelaUsuarios(tableModel);
                    JOptionPane.showMessageDialog(dialogo, "Usuário excluído com sucesso!");
                }
            } else {
                JOptionPane.showMessageDialog(dialogo, "Por favor, selecione um usuário na tabela para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });

        botaoEditar.addActionListener(e -> {
            int selectedRow = tabelaUsuarios.getSelectedRow();
            if (selectedRow >= 0) {
                int idUsuario = (int) tableModel.getValueAt(selectedRow, 0);
                Usuario usuarioParaEditar = usuarioDAO.buscarPorId(idUsuario);

                if (usuarioParaEditar != null) {
                    abrirDialogoEdicaoUsuario(usuarioParaEditar, tableModel);
                } else {
                    JOptionPane.showMessageDialog(dialogo, "Não foi possível encontrar os dados do usuário selecionado.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(dialogo, "Por favor, selecione um usuário na tabela para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });

        dialogo.setLocationRelativeTo(janela);
        dialogo.setVisible(true);
    }

    private void abrirDialogoEdicaoUsuario(Usuario usuario, DefaultTableModel tableModel) {
        JDialog dialogoEdicao = new JDialog(janela, "Editando Usuário: " + usuario.getNome(), true);
        dialogoEdicao.setSize(400, 300);
        dialogoEdicao.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField campoNome = new JTextField(usuario.getNome(), 20);
        JTextField campoEmail = new JTextField(usuario.getEmail(), 20);

        JComboBox<String> comboTipo = new JComboBox<>(new String[]{"comum", "admin"});
        comboTipo.setSelectedItem(usuario.getTipoUsuario());

        gbc.gridx = 0; gbc.gridy = 0; dialogoEdicao.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; dialogoEdicao.add(campoNome, gbc);

        gbc.gridx = 0; gbc.gridy = 1; dialogoEdicao.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; dialogoEdicao.add(campoEmail, gbc);

        gbc.gridx = 0; gbc.gridy = 2; dialogoEdicao.add(new JLabel("Tipo:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; dialogoEdicao.add(comboTipo, gbc);

        JButton botaoSalvar = new JButton("Salvar Alterações");
        gbc.gridx = 1; gbc.gridy = 3;
        dialogoEdicao.add(botaoSalvar, gbc);

        botaoSalvar.addActionListener(e -> {
            try {
                usuario.setNome(campoNome.getText());
                usuario.setEmail(campoEmail.getText());
                usuario.setTipoUsuario((String) comboTipo.getSelectedItem());

                usuarioDAO.atualizarPerfilAdmin(usuario);

                JOptionPane.showMessageDialog(dialogoEdicao, "Usuário atualizado com sucesso!");
                atualizarTabelaUsuarios(tableModel);
                dialogoEdicao.dispose();

            } catch (Exception ex) {
                ex.printStackTrace();

                JOptionPane.showMessageDialog(dialogoEdicao,
                        "Ocorreu um erro ao atualizar:\n" + ex.getMessage(),
                        "Erro de Atualização",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        dialogoEdicao.setLocationRelativeTo(janela);
        dialogoEdicao.setVisible(true);
    }

    private void atualizarTabelaUsuarios(DefaultTableModel tableModel) {
        tableModel.setRowCount(0);
        List<Usuario> usuarios = usuarioDAO.listar();
        for (Usuario usuario : usuarios) {
            tableModel.addRow(new Object[]{
                    usuario.getId(),
                    usuario.getNome(),
                    usuario.getEmail(),
                    usuario.getTipoUsuario()
            });
        }
    }

    private void abrirMenuEventosAdmin() {
        String[] opcoes = {
                "1 - Listar Todos os Eventos",
                "2 - Editar Evento",
                "3 - Excluir Evento",
                "0 - Voltar"
        };

        while (true) {
            String escolha = (String) JOptionPane.showInputDialog(
                    this,
                    "Escolha uma opção:",
                    "Menu de Eventos (Admin)",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    opcoes,
                    opcoes[0]);

            if (escolha == null || escolha.startsWith("0")) break;

            switch (escolha.charAt(0)) {
                case '1':
                    listarTodosEventos();
                    break;
                case '2':
                    editarEventoAdmin();
                    break;
                case '3':
                    excluirEventoAdmin();
                    break;
                default:
                    JOptionPane.showMessageDialog(this, "Opção inválida.");
            }
        }
    }

    private void listarTodosEventos() {
        List<Evento> eventos = eventoDAO.listar();

        if (eventos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhum evento cadastrado.");
            return;
        }

        String[] colunas = {"ID", "Título", "Tipo", "Data", "Usuário"};
        String[][] dados = new String[eventos.size()][colunas.length];

        for (int i = 0; i < eventos.size(); i++) {
            Evento e = eventos.get(i);
            dados[i][0] = String.valueOf(e.getId());
            dados[i][1] = e.getTitulo();
            dados[i][2] = e.getTipo();
            dados[i][3] = new SimpleDateFormat("dd/MM/yyyy").format(e.getData());
            dados[i][4] = e.getUsuario().getNome();
        }

        JTable tabela = new JTable(dados, colunas);
        JScrollPane scrollPane = new JScrollPane(tabela);

        JDialog dialog = new JDialog(janela, "Todos os Eventos", true);
        dialog.add(scrollPane);
        dialog.setSize(700, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void excluirEventoAdmin() {
        List<Evento> eventos = eventoDAO.listar();

        if (eventos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhum evento encontrado.");
            return;
        }

        String[] opcoes = eventos.stream()
                .map(e -> "ID: " + e.getId() + " - " + e.getTitulo() + " (Usuário: " + e.getUsuario().getNome() + ")")
                .toArray(String[]::new);

        String selecionado = (String) JOptionPane.showInputDialog(
                this,
                "Selecione um evento para excluir:",
                "Excluir Evento",
                JOptionPane.PLAIN_MESSAGE,
                null,
                opcoes,
                opcoes[0]
        );

        if (selecionado == null) return;

        int id = Integer.parseInt(selecionado.split(":")[1].split("-")[0].trim());

        int confirm = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja excluir este evento e todos os dados relacionados?",
                "Confirmação",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            eventoDAO.excluir(id);
            JOptionPane.showMessageDialog(this, "Evento excluído com sucesso.");
        }
    }

    private void editarEventoAdmin() {
        List<Evento> eventos = eventoDAO.listar();

        if (eventos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhum evento encontrado.");
            return;
        }

        String[] opcoes = eventos.stream()
                .map(e -> "ID: " + e.getId() + " - " + e.getTitulo() + " (Usuário: " + e.getUsuario().getNome() + ")")
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

        int id = Integer.parseInt(selecionado.split(":")[1].split("-")[0].trim());
        Evento evento = eventoDAO.buscarPorId(id);

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
            eventoDAO.atualizar(evento);
            JOptionPane.showMessageDialog(this, "Evento atualizado com sucesso!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}