package view;

import dao.UsuarioDAO;
import model.Usuario;
import model.UsuarioComum;
import util.Criptografia;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFormattedTextField;
import javax.swing.text.MaskFormatter;

public class TelaComum extends JPanel {

    private JanelaPrincipal janela;
    private UsuarioDAO usuarioDAO;
    private Criptografia criptografia;

    public TelaComum(JanelaPrincipal janela) {
        this.janela = janela;
        this.usuarioDAO = new UsuarioDAO();
        this.criptografia = new Criptografia();

        setLayout(new BorderLayout(20, 20)); // Layout principal com espaçamento
        setBorder(new EmptyBorder(20, 20, 20, 20)); // Margem externa

        // --- TÍTULO (NORTE) ---
        JLabel labelTitulo = new JLabel("Painel do Usuário", SwingConstants.CENTER);
        labelTitulo.setFont(new Font("SansSerif", Font.BOLD, 28));
        add(labelTitulo, BorderLayout.NORTH);

        // --- PAINEL DE BOTÕES (CENTRO) ---
        JPanel painelBotoes = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Botão Meus Dados
        JButton botaoMeusDados = new JButton("Meus Dados Pessoais");
        botaoMeusDados.setFont(new Font("SansSerif", Font.PLAIN, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        painelBotoes.add(botaoMeusDados, gbc);

        // Botão Gerenciar Eventos
        JButton botaoGerenciarEventos = new JButton("Gerenciar Meus Eventos");
        botaoGerenciarEventos.setFont(new Font("SansSerif", Font.PLAIN, 18));
        gbc.gridx = 0;
        gbc.gridy = 1;
        painelBotoes.add(botaoGerenciarEventos, gbc);

        add(painelBotoes, BorderLayout.CENTER);

        // --- LOGOUT (SUL) ---
        JPanel painelLogout = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton botaoLogout = new JButton("Sair (Logout)");
        painelLogout.add(botaoLogout);
        add(painelLogout, BorderLayout.SOUTH);

        // --- AÇÕES DOS BOTÕES ---
        botaoMeusDados.addActionListener(e -> abrirDialogoMeusDados());

        botaoGerenciarEventos.addActionListener(e -> {
            // TODO: Criar a tela de gerenciamento de eventos.
            JOptionPane.showMessageDialog(janela, "Esta tela ainda está em construção!", "Aviso", JOptionPane.INFORMATION_MESSAGE);
        });

        botaoLogout.addActionListener(e -> {
            janela.setUsuarioLogado(null); // Limpa o usuário logado
            janela.setStatusBarText("Aguardando login...");
            janela.getCardLayout().show(janela.getPainelPrincipal(), "login");
        });
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

        // --- Campos pré-preenchidos ---
        JTextField campoNome = new JTextField(usuarioLogado.getNome(), 20);
        JTextField campoIdade = new JTextField(String.valueOf(usuarioLogado.getIdade()), 20);
        JFormattedTextField campoCpf = criarCampoCpfComMascara();
        campoCpf.setText(usuarioLogado.getCpf());
        // A linha .setEditable(false) foi removida daqui

        JTextField campoEmail = new JTextField(usuarioLogado.getEmail(), 20);

        // --- Adicionando componentes à tela ---
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
                // Atualiza os dados no objeto 'usuarioLogado'
                usuarioLogado.setNome(campoNome.getText());
                usuarioLogado.setIdade(Integer.parseInt(campoIdade.getText()));
                usuarioLogado.setEmail(campoEmail.getText());

                // --- LÓGICA PARA ATUALIZAR O CPF ---
                String cpfSemMascara = campoCpf.getText().replaceAll("[^0-9]", "");
                if (cpfSemMascara.length() != 11) {
                    JOptionPane.showMessageDialog(dialogo, "CPF inválido. Preencha todos os 11 dígitos.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                usuarioLogado.setCpf(cpfSemMascara);

                // Pede a senha para confirmar as alterações
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

    /**
     * Cria e retorna um campo de texto formatado para CPF.
     * @return Um JFormattedTextField com a máscara de CPF.
     */
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