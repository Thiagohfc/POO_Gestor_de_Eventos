package view;

import javax.swing.*;

public class TelaComum extends JPanel {
    public TelaComum(JanelaPrincipal janela) {
        add(new JLabel("Painel do Usuário Comum"));

        JButton botaoLogout = new JButton("Logout");
        botaoLogout.addActionListener(e -> {
            janela.setStatusBarText("Aguardando login...");
            janela.getCardLayout().show(janela.getPainelPrincipal(), "login");
        });
        add(botaoLogout);
    }
}