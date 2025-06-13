package view;

import dao.EventoDAO;
import dao.RecursoDAO;
import model.Evento;
import model.Recurso;
import model.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class TelaRecursos extends JPanel {
    private JanelaPrincipal janela;
    private EventoDAO objEventoDAO;
    private RecursoDAO objRecursoDAO;

    public TelaRecursos(JanelaPrincipal janela) {
        this.janela = janela;
        this.objEventoDAO = new EventoDAO();
        this.objRecursoDAO = new RecursoDAO();

        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel labelTitulo = new JLabel("Gestão de Recursos", SwingConstants.CENTER);
        labelTitulo.setFont(new Font("SansSerif", Font.BOLD, 28));
        add(labelTitulo, BorderLayout.NORTH);

        JPanel painelBotoes = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JButton botaoAdicionar = new JButton("Adicionar Recurso a um Evento");
        gbc.gridy = 0;
        painelBotoes.add(botaoAdicionar, gbc);

        JButton botaoEditar = new JButton("Editar Recurso");
        gbc.gridy = 1;
        painelBotoes.add(botaoEditar, gbc);

        JButton botaoExcluir = new JButton("Excluir Recurso");
        gbc.gridy = 2;
        painelBotoes.add(botaoExcluir, gbc);

        JButton botaoListar = new JButton("Listar Recursos de um Evento");
        gbc.gridy = 3;
        painelBotoes.add(botaoListar, gbc);

        add(painelBotoes, BorderLayout.CENTER);

        JButton botaoVoltar = new JButton("Voltar ao Painel Principal");
        JPanel painelVoltar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelVoltar.add(botaoVoltar);
        add(painelVoltar, BorderLayout.SOUTH);

        botaoAdicionar.addActionListener(e -> adicionarRecurso());
        botaoEditar.addActionListener(e -> editarRecurso());
        botaoExcluir.addActionListener(e -> excluirRecurso());
        botaoListar.addActionListener(e -> listarRecursos());
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

    private void adicionarRecurso() {
        Evento eventoSelecionado = selecionarMeuEvento("Adicionar Recurso");
        if (eventoSelecionado == null) return;

        JPanel painel = new JPanel(new GridLayout(0, 2, 10, 10));
        JTextField campoNome = new JTextField();
        JTextField campoQuantidade = new JTextField();
        JTextArea areaDescricao = new JTextArea(3, 20);

        painel.add(new JLabel("Nome do Recurso:")); painel.add(campoNome);
        painel.add(new JLabel("Quantidade:")); painel.add(campoQuantidade);
        painel.add(new JLabel("Descrição:")); painel.add(new JScrollPane(areaDescricao));

        int resultado = JOptionPane.showConfirmDialog(this, painel, "Adicionar Recurso ao Evento: " + eventoSelecionado.getTitulo(), JOptionPane.OK_CANCEL_OPTION);
        if (resultado != JOptionPane.OK_OPTION) return;

        try {
            Recurso recurso = new Recurso();
            recurso.setNome(campoNome.getText());
            recurso.setQuantidade(Integer.parseInt(campoQuantidade.getText()));
            recurso.setDescricao(areaDescricao.getText());
            recurso.setEvento(eventoSelecionado);

            objRecursoDAO.inserir(recurso);
            JOptionPane.showMessageDialog(this, "Recurso adicionado com sucesso!");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Quantidade inválida. Por favor, insira um número.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao adicionar recurso: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editarRecurso() {
        Evento eventoSelecionado = selecionarMeuEvento("Editar Recurso");
        if (eventoSelecionado == null) return;

        List<Recurso> recursosDoEvento = objRecursoDAO.listar().stream()
                .filter(r -> r.getEvento().getId() == eventoSelecionado.getId())
                .collect(Collectors.toList());

        if (recursosDoEvento.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Este evento não possui recursos cadastrados.");
            return;
        }

        String[] opcoesRecursos = recursosDoEvento.stream()
                .map(r -> r.getId() + " - " + r.getNome())
                .toArray(String[]::new);

        String escolha = (String) JOptionPane.showInputDialog(
                this,
                "Selecione o recurso para editar:",
                "Editar Recurso do Evento: " + eventoSelecionado.getTitulo(),
                JOptionPane.PLAIN_MESSAGE,
                null,
                opcoesRecursos,
                opcoesRecursos[0]);

        if (escolha == null) return;

        int recursoId = Integer.parseInt(escolha.split(" - ")[0]);
        Recurso recursoParaEditar = objRecursoDAO.buscarPorId(recursoId);

        JPanel painel = new JPanel(new GridLayout(0, 2, 10, 10));
        JTextField campoNome = new JTextField(recursoParaEditar.getNome());
        JTextField campoQuantidade = new JTextField(String.valueOf(recursoParaEditar.getQuantidade()));
        JTextArea areaDescricao = new JTextArea(recursoParaEditar.getDescricao(), 3, 20);

        painel.add(new JLabel("Nome do Recurso:")); painel.add(campoNome);
        painel.add(new JLabel("Quantidade:")); painel.add(campoQuantidade);
        painel.add(new JLabel("Descrição:")); painel.add(new JScrollPane(areaDescricao));

        int resultado = JOptionPane.showConfirmDialog(this, painel, "Editando Recurso", JOptionPane.OK_CANCEL_OPTION);
        if (resultado != JOptionPane.OK_OPTION) return;

        try {
            recursoParaEditar.setNome(campoNome.getText());
            recursoParaEditar.setQuantidade(Integer.parseInt(campoQuantidade.getText()));
            recursoParaEditar.setDescricao(areaDescricao.getText());

            objRecursoDAO.atualizar(recursoParaEditar);
            JOptionPane.showMessageDialog(this, "Recurso atualizado com sucesso!");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Quantidade inválida. Por favor, insira um número.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar recurso: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluirRecurso() {
        Evento eventoSelecionado = selecionarMeuEvento("Excluir Recurso");
        if (eventoSelecionado == null) return;

        List<Recurso> recursosDoEvento = objRecursoDAO.listar().stream()
                .filter(r -> r.getEvento().getId() == eventoSelecionado.getId())
                .collect(Collectors.toList());

        if (recursosDoEvento.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Este evento não possui recursos para excluir.");
            return;
        }

        String[] opcoesRecursos = recursosDoEvento.stream()
                .map(r -> r.getId() + " - " + r.getNome())
                .toArray(String[]::new);

        String escolha = (String) JOptionPane.showInputDialog(
                this,
                "Selecione o recurso para excluir:",
                "Excluir Recurso do Evento: " + eventoSelecionado.getTitulo(),
                JOptionPane.PLAIN_MESSAGE,
                null,
                opcoesRecursos,
                opcoesRecursos[0]);

        if (escolha == null) return;

        int recursoId = Integer.parseInt(escolha.split(" - ")[0]);

        int confirmacao = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir este recurso?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        if (confirmacao == JOptionPane.YES_OPTION) {
            objRecursoDAO.excluir(recursoId);
            JOptionPane.showMessageDialog(this, "Recurso excluído com sucesso!");
        }
    }

    private void listarRecursos() {
        Evento eventoSelecionado = selecionarMeuEvento("Listar Recursos");
        if (eventoSelecionado == null) return;

        List<Recurso> recursosDoEvento = objRecursoDAO.listar().stream()
                .filter(r -> r.getEvento().getId() == eventoSelecionado.getId())
                .collect(Collectors.toList());

        if (recursosDoEvento.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Não há recursos cadastrados para este evento.");
            return;
        }

        String[] colunas = {"ID", "Nome", "Quantidade", "Descrição"};
        String[][] dados = new String[recursosDoEvento.size()][colunas.length];

        for (int i = 0; i < recursosDoEvento.size(); i++) {
            Recurso r = recursosDoEvento.get(i);
            dados[i][0] = String.valueOf(r.getId());
            dados[i][1] = r.getNome();
            dados[i][2] = String.valueOf(r.getQuantidade());
            dados[i][3] = r.getDescricao();
        }

        JTable tabela = new JTable(dados, colunas);
        JScrollPane scrollPane = new JScrollPane(tabela);

        JDialog dialogo = new JDialog(janela, "Recursos do Evento: " + eventoSelecionado.getTitulo(), true);
        dialogo.add(scrollPane);
        dialogo.setSize(600, 300);
        dialogo.setLocationRelativeTo(janela);
        dialogo.setVisible(true);
    }
}
