package view;

import javax.swing.*;

public class TelaAdmin extends JPanel {
    public TelaAdmin(JanelaPrincipal janela) {
        add(new JLabel("Painel do Administrador"));

        JButton botaoLogout = new JButton("Logout");
        botaoLogout.addActionListener(e -> {
            janela.setStatusBarText("Aguardando login...");
            janela.getCardLayout().show(janela.getPainelPrincipal(), "login");
        });
        add(botaoLogout);
    }
}