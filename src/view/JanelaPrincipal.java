package view;

import model.Usuario;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JanelaPrincipal extends JFrame {

    private CardLayout cardLayout;
    private JPanel painelPrincipal;
    private JLabel labelStatus;
    private Usuario usuarioLogado;
    private TelaRecursos telaRecursos;

    public JanelaPrincipal() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        super("EventSys - Sistema de Gerenciamento de Eventos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JMenuBar menuBar = criarBarraDeMenu();
        JPanel statusBar = criarBarraDeStatus();

        cardLayout = new CardLayout();
        painelPrincipal = new JPanel(cardLayout);

        TelaLogin telaLogin = new TelaLogin(this);
        TelaAdmin telaAdmin = new TelaAdmin(this);
        TelaComum telaComum = new TelaComum(this);
        TelaAtracao telaAtracao = new TelaAtracao(this);
        TelaRecursos telaRecursos = new TelaRecursos(this);

        painelPrincipal.add(telaLogin, "login");
        painelPrincipal.add(telaAdmin, "admin");
        painelPrincipal.add(telaComum, "comum");
        painelPrincipal.add(telaAtracao, "atracoes");
        painelPrincipal.add(telaRecursos, "recursos");

        setJMenuBar(menuBar);
        add(painelPrincipal, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);

        cardLayout.show(painelPrincipal, "login");
        setStatusBarText("Aguardando login...");
    }

    private JMenuBar criarBarraDeMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menuArquivo = new JMenu("Arquivo");
        JMenuItem itemSair = new JMenuItem("Sair");
        itemSair.addActionListener(e -> System.exit(0));
        menuArquivo.add(itemSair);

        JMenu menuAjuda = new JMenu("Ajuda");
        JMenuItem itemSobre = new JMenuItem("Sobre");
        itemSobre.addActionListener(e ->
                JOptionPane.showMessageDialog(this,
                        "EventSys v1.0\nDesenvolvido para gerenciar seus eventos.",
                        "Sobre o Sistema",
                        JOptionPane.INFORMATION_MESSAGE)
        );
        menuAjuda.add(itemSobre);

        menuBar.add(menuArquivo);
        menuBar.add(menuAjuda);
        return menuBar;
    }

    private JPanel criarBarraDeStatus() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(new BevelBorder(BevelBorder.LOWERED));
        labelStatus = new JLabel("Pronto.");
        labelStatus.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        statusBar.add(labelStatus, BorderLayout.CENTER);

        JLabel labelDataHora = new JLabel();
        labelDataHora.setHorizontalAlignment(SwingConstants.RIGHT);
        labelDataHora.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        Timer timer = new Timer(1000, e -> {
            labelDataHora.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
        });
        timer.start();
        statusBar.add(labelDataHora, BorderLayout.EAST);

        return statusBar;
    }

    public void setStatusBarText(String texto) {
        labelStatus.setText(texto);
    }

    public JPanel getPainelPrincipal() {
        return painelPrincipal;
    }

    public CardLayout getCardLayout() {
        return cardLayout;
    }

    public void setUsuarioLogado(Usuario usuario) {
        this.usuarioLogado = usuario;
    }

    public Usuario getUsuarioLogado() {
        return this.usuarioLogado;
    }
}