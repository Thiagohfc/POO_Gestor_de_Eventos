package view;

import dao.UsuarioDAO;
import model.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TelaAdmin extends JPanel {

    private JanelaPrincipal janela;
    private UsuarioDAO usuarioDAO;

    public TelaAdmin(JanelaPrincipal janela) {
        this.janela = janela;
        this.usuarioDAO = new UsuarioDAO();

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

        add(painelBotoes, BorderLayout.CENTER);

        JPanel painelLogout = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton botaoLogout = new JButton("Sair (Logout)");
        painelLogout.add(botaoLogout);
        add(painelLogout, BorderLayout.SOUTH);

        botaoGerenciarUsuarios.addActionListener(e -> abrirDialogoGerenciarUsuarios());

        botaoGerenciarEventos.addActionListener(e -> {
            JOptionPane.showMessageDialog(janela, "Esta tela ainda está em construção!", "Aviso", JOptionPane.INFORMATION_MESSAGE);
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
}