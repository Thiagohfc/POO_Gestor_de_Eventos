package view;

import dao.AtracaoDAO;
import dao.EventoDAO;
import dao.RecursoDAO;
import model.Atracao;
import model.Evento;
import model.Recurso;
import model.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.stream.Collectors;

public class TelaAtracao extends JPanel {
    private JanelaPrincipal janela;
    private EventoDAO objEventoDAO;
    private AtracaoDAO objAtracaoDAO;

    public TelaAtracao(JanelaPrincipal janela) {
        this.janela = janela;
        this.objEventoDAO = new EventoDAO();
        this.objAtracaoDAO = new AtracaoDAO();

        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel labelTitulo = new JLabel("Gestão de Atrações", SwingConstants.CENTER);
        labelTitulo.setFont(new Font("SansSerif", Font.BOLD, 28));
        add(labelTitulo, BorderLayout.NORTH);

        JPanel painelBotoes = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JButton botaoAdicionar = new JButton("Adicionar Atração a um Evento");
        gbc.gridy = 0;
        painelBotoes.add(botaoAdicionar, gbc);

        JButton botaoEditar = new JButton("Editar Atração");
        gbc.gridy = 1;
        painelBotoes.add(botaoEditar, gbc);

        JButton botaoExcluir = new JButton("Excluir Atração");
        gbc.gridy = 2;
        painelBotoes.add(botaoExcluir, gbc);

        JButton botaoListar = new JButton("Listar Atrações de um Evento");
        gbc.gridy = 3;
        painelBotoes.add(botaoListar, gbc);

        add(painelBotoes, BorderLayout.CENTER);

        JButton botaoVoltar = new JButton("Voltar ao Painel Principal");
        JPanel painelVoltar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelVoltar.add(botaoVoltar);
        add(painelVoltar, BorderLayout.SOUTH);

        botaoAdicionar.addActionListener(e -> adicionarAtracao());
        botaoEditar.addActionListener(e -> editarAtracao());
        botaoExcluir.addActionListener(e -> excluirAtracao());
        botaoListar.addActionListener(e -> listarAtracao());
        botaoVoltar.addActionListener(e -> janela.getCardLayout().show(janela.getPainelPrincipal(), "comum"));
    }

    private Evento selecionarMeuEvento(String tituloDialogo) {
        Usuario usuarioLogado = janela.getUsuarioLogado();
        if (usuarioLogado == null) {
            JOptionPane.showMessageDialog(this, "Sessão expirada. Por favor, faça login novamente.", "Erro", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        List<Evento> meusEventos = objEventoDAO.listar().stream()
                .filter(e -> e.getUsuario().getId() == usuarioLogado.getId())
                .collect(Collectors.toList());

        if (meusEventos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Você não possui eventos para gerenciar.");
            return null;
        }

        String[] opcoesEventos = meusEventos.stream()
                .map(e -> e.getId() + " - " + e.getTitulo())
                .toArray(String[]::new);

        String escolha = (String) JOptionPane.showInputDialog(
                this,
                "Primeiro, selecione o evento:",
                tituloDialogo,
                JOptionPane.PLAIN_MESSAGE,
                null,
                opcoesEventos,
                opcoesEventos[0]);

        if (escolha == null) return null;

        int eventoId = Integer.parseInt(escolha.split(" - ")[0]);
        return objEventoDAO.buscarPorId(eventoId);
    }

    private void adicionarAtracao() {
        Evento eventoSelecionado = selecionarMeuEvento("Adicionar Atração");
        if (eventoSelecionado == null) return;

        JPanel painel = new JPanel(new GridLayout(0, 2, 10, 10));
        JTextField campoNome = new JTextField();
        JTextField campoTipo = new JTextField();
        JTextField campoHorario = new JTextField();

        painel.add(new JLabel("Nome da Atração:")); painel.add(campoNome);
        painel.add(new JLabel("Tipo:")); painel.add(campoTipo);
        painel.add(new JLabel("Horário (00:00):")); painel.add(campoHorario);

        int resultado = JOptionPane.showConfirmDialog(this, painel, "Adicionar Atração ao Evento: " + eventoSelecionado.getTitulo(), JOptionPane.OK_CANCEL_OPTION);
        if (resultado != JOptionPane.OK_OPTION) return;

        try {
            Atracao atracao = new Atracao();
            atracao.setNome(campoNome.getText());
            atracao.setTipo(campoTipo.getText());
            atracao.setHorario(campoHorario.getText());
            atracao.setEvento(eventoSelecionado);

            objAtracaoDAO.inserir(atracao);
            JOptionPane.showMessageDialog(this, "Atração adicionada com sucesso!");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Horário inválido. Por favor, insira novamente.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao adicionar atração: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editarAtracao() {
        Evento eventoSelecionado = selecionarMeuEvento("Editar Atração");
        if (eventoSelecionado == null) return;

        List<Atracao> atracaoDoEvento = objAtracaoDAO.listar().stream()
                .filter(r -> r.getEvento().getId() == eventoSelecionado.getId())
                .collect(Collectors.toList());

        if (atracaoDoEvento.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Este evento não possui atrações cadastradas.");
            return;
        }

        String[] opcoesAtracao = atracaoDoEvento.stream()
                .map(r -> r.getId() + " - " + r.getNome())
                .toArray(String[]::new);

        String escolha = (String) JOptionPane.showInputDialog(
                this,
                "Selecione a atração para editar:",
                "Editar Atração do Evento: " + eventoSelecionado.getTitulo(),
                JOptionPane.PLAIN_MESSAGE,
                null,
                opcoesAtracao,
                opcoesAtracao[0]);

        if (escolha == null) return;

        int atracaoId = Integer.parseInt(escolha.split(" - ")[0]);
        Atracao atracaoParaEditar = objAtracaoDAO.buscarPorId(atracaoId);

        JPanel painel = new JPanel(new GridLayout(0, 2, 10, 10));
        JTextField campoNome = new JTextField(atracaoParaEditar.getNome());
        JTextField campoTipo = new JTextField(atracaoParaEditar.getTipo());
        JTextField campoHorario = new JTextField(atracaoParaEditar.getHorario());

        painel.add(new JLabel("Nome da Atração:")); painel.add(campoNome);
        painel.add(new JLabel("Tipo:")); painel.add(campoTipo);
        painel.add(new JLabel("Horário:")); painel.add(campoHorario);

        int resultado = JOptionPane.showConfirmDialog(this, painel, "Editando Atração", JOptionPane.OK_CANCEL_OPTION);
        if (resultado != JOptionPane.OK_OPTION) return;

        try {
            atracaoParaEditar.setNome(campoNome.getText());
            atracaoParaEditar.setTipo(campoTipo.getText());
            atracaoParaEditar.setHorario(campoHorario.getText());

            objAtracaoDAO.atualizar(atracaoParaEditar);
            JOptionPane.showMessageDialog(this, "Atração atualizada com sucesso!");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Horário inválido. Por favor, insira novamente.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar atração: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluirAtracao() {
        Evento eventoSelecionado = selecionarMeuEvento("Excluir Atração");
        if (eventoSelecionado == null) return;

        List<Atracao> atracaoDoEvento = objAtracaoDAO.listar().stream()
                .filter(r -> r.getEvento().getId() == eventoSelecionado.getId())
                .collect(Collectors.toList());

        if (atracaoDoEvento.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Este evento não possui atrações para excluir.");
            return;
        }

        String[] opcoesAtracao = atracaoDoEvento.stream()
                .map(r -> r.getId() + " - " + r.getNome())
                .toArray(String[]::new);

        String escolha = (String) JOptionPane.showInputDialog(
                this,
                "Selecione a atração para excluir:",
                "Excluir Atração do Evento: " + eventoSelecionado.getTitulo(),
                JOptionPane.PLAIN_MESSAGE,
                null,
                opcoesAtracao,
                opcoesAtracao[0]);

        if (escolha == null) return;

        int atracaoId = Integer.parseInt(escolha.split(" - ")[0]);

        int confirmacao = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir esta atração?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        if (confirmacao == JOptionPane.YES_OPTION) {
            objAtracaoDAO.excluir(atracaoId);
            JOptionPane.showMessageDialog(this, "Atração excluída com sucesso!");
        }
    }

    private void listarAtracao() {
        Evento eventoSelecionado = selecionarMeuEvento("Listar Atrações");
        if (eventoSelecionado == null) return;

        List<Atracao> atracaoDoEvento = objAtracaoDAO.listar().stream()
                .filter(r -> r.getEvento().getId() == eventoSelecionado.getId())
                .collect(Collectors.toList());

        if (atracaoDoEvento.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Não há atrações cadastradas para este evento.");
            return;
        }

        String[] colunas = {"ID", "Nome", "Tipo", "Horário"};
        String[][] dados = new String[atracaoDoEvento.size()][colunas.length];

        for (int i = 0; i < atracaoDoEvento.size(); i++) {
            Atracao r = atracaoDoEvento.get(i);
            dados[i][0] = String.valueOf(r.getId());
            dados[i][1] = r.getNome();
            dados[i][2] = String.valueOf(r.getTipo());
            dados[i][3] = r.getHorario();
        }

        JTable tabela = new JTable(dados, colunas);
        JScrollPane scrollPane = new JScrollPane(tabela);

        JDialog dialogo = new JDialog(janela, "Atrações do Evento: " + eventoSelecionado.getTitulo(), true);
        dialogo.add(scrollPane);
        dialogo.setSize(600, 300);
        dialogo.setLocationRelativeTo(janela);
        dialogo.setVisible(true);
    }
}