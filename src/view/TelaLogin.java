package view;

import dao.UsuarioDAO;
import model.Usuario;
import util.Criptografia;
import model.UsuarioComum;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFormattedTextField;
import javax.swing.text.MaskFormatter;

public class TelaLogin extends JPanel {

    // Componentes da Interface Gráfica
    private JTextField campoEmail;
    private JPasswordField campoSenha;
    private JButton botaoLogin;
    private JButton botaoCadastro;

    // Referência à janela principal para poder controlá-la
    private JanelaPrincipal janela;

    // Componentes da Lógica de Negócio (seus DAOs e Utils)
    private UsuarioDAO usuarioDAO;
    private Criptografia criptografia;

    public TelaLogin(JanelaPrincipal janela) {
        this.janela = janela;
        this.usuarioDAO = new UsuarioDAO();
        this.criptografia = new Criptografia();

        // Configura o layout do painel
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Espaçamento entre os componentes

        // Título da tela
        JLabel labelTitulo = new JLabel("Bem-vindo ao EventSys", SwingConstants.CENTER);
        labelTitulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Ocupa duas colunas
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(labelTitulo, gbc);

        // Campo Email
        gbc.gridwidth = 1; // Volta ao padrão
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        campoEmail = new JTextField(25);
        add(campoEmail, gbc);

        // Campo Senha
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Senha:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        campoSenha = new JPasswordField(25);
        add(campoSenha, gbc);

        // Painel de botões para organizar "Login" e "Cadastrar-se"
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        botaoLogin = new JButton("Entrar");
        botaoCadastro = new JButton("Cadastrar-se");
        painelBotoes.add(botaoLogin);
        painelBotoes.add(botaoCadastro);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        add(painelBotoes, gbc);

        // --- AÇÕES DOS BOTÕES ---

        // Ação do Botão de Login
        botaoLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fazerLogin();
            }
        });

        // Permite logar pressionando ENTER no campo de senha
        campoSenha.addActionListener(e -> fazerLogin());


        // Ação do Botão de Cadastro
        botaoCadastro.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirDialogoCadastro();
            }
        });
    }

    /**
     * Contém a lógica de autenticação do usuário.
     */
    private void fazerLogin() {
        String email = campoEmail.getText().trim();
        String senha = new String(campoSenha.getPassword());

        if (email.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, preencha email e senha.", "Campos Vazios", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Lógica de login extraída da sua 'main' de terminal
        String senhaCriptografada = criptografia.encriptarMD5(senha);
        Usuario usuario = usuarioDAO.autenticar(email, senhaCriptografada);

        if (usuario != null) {
            // Login bem-sucedido!
            janela.setStatusBarText("Login bem-sucedido! Bem-vindo(a), " + usuario.getNome() + "!");

            // Limpa os campos após o login
            campoEmail.setText("");
            campoSenha.setText("");

            // Troca para a tela correta (admin ou comum)
            if ("admin".equalsIgnoreCase(usuario.getTipoUsuario())) {
                janela.getCardLayout().show(janela.getPainelPrincipal(), "admin");
            } else {
                janela.getCardLayout().show(janela.getPainelPrincipal(), "comum");
            }


        } else {
            // Falha no login
            JOptionPane.showMessageDialog(this, "Email ou senha inválidos.", "Erro de Login", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JFormattedTextField criarCampoCpfComMascara() {
        try {
            MaskFormatter mascaraCpf = new MaskFormatter("###.###.###-##");
            mascaraCpf.setPlaceholderCharacter('_');
            return new JFormattedTextField(mascaraCpf);
        } catch (java.text.ParseException e) {
            // Se a máscara falhar por algum motivo, retorna um campo simples
            // e notifica o usuário ou loga o erro.
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao criar máscara de CPF.", "Erro", JOptionPane.ERROR_MESSAGE);
            return new JFormattedTextField();
        }
    }

    /**
     * Cria e exibe uma nova janela (JDialog) para o cadastro de usuários.
     */
    private void abrirDialogoCadastro() {
        JDialog dialogoCadastro = new JDialog(janela, "Cadastro de Novo Usuário", true);
        dialogoCadastro.setLayout(new GridBagLayout());
        // REMOVA a linha dialogoCadastro.setLocationRelativeTo(janela); daqui.

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Campos do formulário de cadastro ---
        JTextField campoNome = new JTextField(20);
        JTextField campoIdade = new JTextField(20);
        final JFormattedTextField campoCpf = criarCampoCpfComMascara();
        JTextField campoEmailCadastro = new JTextField(20);
        JPasswordField campoSenhaCadastro = new JPasswordField(20);

        // --- Adicionando componentes à tela ---
        gbc.gridx = 0; gbc.gridy = 0; dialogoCadastro.add(new JLabel("Nome Completo:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; dialogoCadastro.add(campoNome, gbc);

        gbc.gridx = 0; gbc.gridy = 1; dialogoCadastro.add(new JLabel("Idade:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; dialogoCadastro.add(campoIdade, gbc);

        gbc.gridx = 0; gbc.gridy = 2; dialogoCadastro.add(new JLabel("CPF:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; dialogoCadastro.add(campoCpf, gbc);

        gbc.gridx = 0; gbc.gridy = 3; dialogoCadastro.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; dialogoCadastro.add(campoEmailCadastro, gbc);

        gbc.gridx = 0; gbc.gridy = 4; dialogoCadastro.add(new JLabel("Senha:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; dialogoCadastro.add(campoSenhaCadastro, gbc);

        JButton botaoSalvar = new JButton("Salvar Cadastro");
        gbc.gridx = 1; gbc.gridy = 5;
        dialogoCadastro.add(botaoSalvar, gbc);

        // Ação do botão Salvar dentro do diálogo
        botaoSalvar.addActionListener(e -> {
            try {
                UsuarioComum novoUsuario = new UsuarioComum();
                novoUsuario.setNome(campoNome.getText());
                novoUsuario.setIdade(Integer.parseInt(campoIdade.getText()));

                String cpfSemMascara = campoCpf.getText().replaceAll("[^0-9]", "");

                if (cpfSemMascara.length() != 11) {
                    JOptionPane.showMessageDialog(dialogoCadastro, "CPF inválido. Preencha todos os 11 dígitos.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                novoUsuario.setCpf(cpfSemMascara);

                novoUsuario.setEmail(campoEmailCadastro.getText());
                novoUsuario.setSenha(criptografia.encriptarMD5(new String(campoSenhaCadastro.getPassword())));
                novoUsuario.setTipoUsuario("comum");

                usuarioDAO.inserir(novoUsuario);

                JOptionPane.showMessageDialog(dialogoCadastro, "Cadastro realizado com sucesso!");
                dialogoCadastro.dispose();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialogoCadastro, "Idade inválida. Por favor, insira um número.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialogoCadastro, "Ocorreu um erro ao cadastrar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        // --- ORDEM CORRETA PARA EXIBIÇÃO ---
        dialogoCadastro.pack(); // 1. Primeiro, calcula o tamanho.
        dialogoCadastro.setLocationRelativeTo(janela); // 2. Depois, centraliza.
        dialogoCadastro.setVisible(true); // 3. Por último, mostra.
    }

}