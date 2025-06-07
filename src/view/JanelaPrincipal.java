package view;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JanelaPrincipal extends JFrame {

    private CardLayout cardLayout;
    private JPanel painelPrincipal;
    private JLabel labelStatus; // Rótulo para a barra de status

    public JanelaPrincipal() {
        // --- ETAPA 0: Define o Look and Feel para o visual nativo do SO ---
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // --- ETAPA 1: Configurações da Janela Principal ---
        super("EventSys - Sistema de Gerenciamento de Eventos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(800, 600)); // Define um tamanho mínimo
        setLocationRelativeTo(null);
        setLayout(new BorderLayout()); // Define o layout principal como BorderLayout

        // --- ETAPA 2: Criação dos Componentes da UI ---
        JMenuBar menuBar = criarBarraDeMenu();
        JPanel statusBar = criarBarraDeStatus();

        // O painel principal com CardLayout que já tínhamos
        cardLayout = new CardLayout();
        painelPrincipal = new JPanel(cardLayout);

        // --- ETAPA 3: Adiciona as Telas (JPanels) ao CardLayout ---
        // Agora passamos a instância da própria janela para as telas filhas.
        // Isso permite que elas chamem métodos da JanelaPrincipal, como o de atualizar a barra de status.
        TelaLogin telaLogin = new TelaLogin(this);
        TelaAdmin telaAdmin = new TelaAdmin(this);
        TelaComum telaComum = new TelaComum(this);

        painelPrincipal.add(telaLogin, "login");
        painelPrincipal.add(telaAdmin, "admin");
        painelPrincipal.add(telaComum, "comum");

        // --- ETAPA 4: Montagem da Janela ---
        setJMenuBar(menuBar); // Adiciona a barra de menus ao topo
        add(painelPrincipal, BorderLayout.CENTER); // Adiciona o painel com as telas ao centro
        add(statusBar, BorderLayout.SOUTH); // Adiciona a barra de status ao rodapé

        // --- ETAPA 5: Inicia na tela de login ---
        cardLayout.show(painelPrincipal, "login");
        setStatusBarText("Aguardando login...");
    }

    // --- MÉTODOS AUXILIARES PARA ORGANIZAR A CRIAÇÃO DA UI ---

    private JMenuBar criarBarraDeMenu() {
        JMenuBar menuBar = new JMenuBar();

        // Menu "Arquivo"
        JMenu menuArquivo = new JMenu("Arquivo");
        JMenuItem itemSair = new JMenuItem("Sair");
        itemSair.addActionListener(e -> System.exit(0));
        menuArquivo.add(itemSair);

        // Menu "Ajuda"
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
        statusBar.setBorder(new BevelBorder(BevelBorder.LOWERED)); // Efeito de borda

        // Rótulo para o texto principal
        labelStatus = new JLabel("Pronto.");
        labelStatus.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5)); // Margem interna
        statusBar.add(labelStatus, BorderLayout.CENTER);

        // Rótulo para a data/hora
        JLabel labelDataHora = new JLabel();
        labelDataHora.setHorizontalAlignment(SwingConstants.RIGHT);
        labelDataHora.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        // Timer para atualizar a data/hora a cada segundo
        Timer timer = new Timer(1000, e -> {
            labelDataHora.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
        });
        timer.start();
        statusBar.add(labelDataHora, BorderLayout.EAST);

        return statusBar;
    }

    // --- MÉTODOS PÚBLICOS PARA CONTROLE DA JANELA ---

    /**
     * Altera o texto exibido na barra de status.
     * @param texto O texto a ser exibido.
     */
    public void setStatusBarText(String texto) {
        labelStatus.setText(texto);
    }

    /**
     * Retorna o painel principal (container das telas).
     * @return O JPanel principal.
     */
    public JPanel getPainelPrincipal() {
        return painelPrincipal;
    }

    /**
     * Retorna o CardLayout para permitir a troca de telas.
     * @return O CardLayout da janela.
     */
    public CardLayout getCardLayout() {
        return cardLayout;
    }
}