package view;

import dao.EnderecoDAO;
import dao.EventoDAO;
import model.Endereco;
import model.Evento;
import model.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class TelaEndereco extends JPanel {
    private JanelaPrincipal janela;
    private EventoDAO objEventoDAO;
    private EnderecoDAO objEnderecoDAO;

    public TelaEndereco(JanelaPrincipal janela) {
        this.janela = janela;
        this.objEventoDAO = new EventoDAO();
        this.objEnderecoDAO = new EnderecoDAO();

        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel labelTitulo = new JLabel("Gestão de Endereços", SwingConstants.CENTER);
        labelTitulo.setFont(new Font("SansSerif", Font.BOLD, 28));
        add(labelTitulo, BorderLayout.NORTH);

        JPanel painelBotoes = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JButton botaoEditar = new JButton("Editar Endereço de Evento");
        gbc.gridy = 0;
        painelBotoes.add(botaoEditar, gbc);

        JButton botaoListar = new JButton("Listar Endereço de Evento");
        gbc.gridy = 1;
        painelBotoes.add(botaoListar, gbc);

        add(painelBotoes, BorderLayout.CENTER);

        JButton botaoVoltar = new JButton("Voltar ao Painel Principal");
        JPanel painelVoltar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelVoltar.add(botaoVoltar);
        add(painelVoltar, BorderLayout.SOUTH);

        botaoEditar.addActionListener(e -> editarEndereco());
        botaoListar.addActionListener(e -> listarEndereco());
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
                "Selecione o evento:",
                tituloDialogo,
                JOptionPane.PLAIN_MESSAGE,
                null,
                opcoesEventos,
                opcoesEventos[0]);

        if (escolha == null) return null;

        int eventoId = Integer.parseInt(escolha.split(" - ")[0]);
        return objEventoDAO.buscarPorId(eventoId);
    }

    private void editarEndereco() {
        Evento eventoSelecionado = selecionarMeuEvento("Editar Endereço");
        if (eventoSelecionado == null) return;

        Endereco endereco = objEnderecoDAO.buscarPorEventoId(eventoSelecionado.getId());
        if (endereco == null) endereco = new Endereco();

        JPanel painel = new JPanel(new GridLayout(0, 2, 10, 10));
        JTextField campoEstado = new JTextField(endereco.getEstado() != null ? endereco.getEstado() : "");
        JTextField campoCidade = new JTextField(endereco.getCidade() != null ? endereco.getCidade() : "");
        JTextField campoRua = new JTextField(endereco.getRua() != null ? endereco.getRua() : "");
        JTextField campoNumero = new JTextField(endereco.getNumero() != null ? endereco.getNumero() : "");
        JTextField campoLotacao = new JTextField(String.valueOf(endereco.getLotacao()));

        painel.add(new JLabel("Estado:")); painel.add(campoEstado);
        painel.add(new JLabel("Cidade:")); painel.add(campoCidade);
        painel.add(new JLabel("Rua:")); painel.add(campoRua);
        painel.add(new JLabel("Número:")); painel.add(campoNumero);
        painel.add(new JLabel("Lotação:")); painel.add(campoLotacao);

        int resultado = JOptionPane.showConfirmDialog(this, painel, "Editar Endereço do Evento: " + eventoSelecionado.getTitulo(), JOptionPane.OK_CANCEL_OPTION);
        if (resultado != JOptionPane.OK_OPTION) return;

        try {
            endereco.setEstado(campoEstado.getText());
            endereco.setCidade(campoCidade.getText());
            endereco.setRua(campoRua.getText());
            endereco.setNumero(campoNumero.getText());
            endereco.setLotacao(Integer.parseInt(campoLotacao.getText()));
            endereco.setEvento(eventoSelecionado);

            if (endereco.getId() == 0) objEnderecoDAO.inserir(endereco);
            else objEnderecoDAO.atualizar(endereco);

            JOptionPane.showMessageDialog(this, "Endereço salvo com sucesso!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar endereço: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void listarEndereco() {
        Evento eventoSelecionado = selecionarMeuEvento("Listar Endereço");
        if (eventoSelecionado == null) return;

        Endereco endereco = objEnderecoDAO.buscarPorEventoId(eventoSelecionado.getId());

        String[][] dados = {{
                String.valueOf(endereco.getId()),
                endereco.getEstado(),
                endereco.getCidade(),
                endereco.getRua(),
                endereco.getNumero(),
                String.valueOf(endereco.getLotacao())
        }};

        String[] colunas = {"ID", "Estado", "Cidade", "Rua", "Número", "Lotação"};
        JTable tabela = new JTable(dados, colunas);
        JScrollPane scrollPane = new JScrollPane(tabela);

        JDialog dialogo = new JDialog(janela, "Endereço do Evento: " + eventoSelecionado.getTitulo(), true);
        dialogo.add(scrollPane);
        dialogo.setSize(700, 200);
        dialogo.setLocationRelativeTo(janela);
        dialogo.setVisible(true);
    }
}