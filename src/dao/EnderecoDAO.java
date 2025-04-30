package dao;

import model.Endereco;
import model.Evento;
import util.Conexao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnderecoDAO {
    private Connection conn;

    public EnderecoDAO() {
        conn = Conexao.getConexao();
    }

    public void inserir(Endereco endereco) {
        String sql = "INSERT INTO endereco (endereco_estado, endereco_cidade, endereco_rua, endereco_numero, endereco_lotacao, evento_evento_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, endereco.getEstado());
            stmt.setString(2, endereco.getCidade());
            stmt.setString(3, endereco.getRua());
            stmt.setString(4, endereco.getNumero());
            stmt.setInt(5, endereco.getLotacao());
            stmt.setInt(6, endereco.getEvento().getId());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                endereco.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Endereco buscarPorId(int id) {
        String sql = "SELECT * FROM endereco WHERE endereco_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Endereco endereco = new Endereco();
                endereco.setId(rs.getInt("endereco_id"));
                endereco.setEstado(rs.getString("endereco_estado"));
                endereco.setCidade(rs.getString("endereco_cidade"));
                endereco.setRua(rs.getString("endereco_rua"));
                endereco.setNumero(rs.getString("endereco_numero"));
                endereco.setLotacao(rs.getInt("endereco_lotacao"));

                EventoDAO eventoDAO = new EventoDAO();
                Evento evento = eventoDAO.buscarPorId(rs.getInt("evento_evento_id"));
                endereco.setEvento(evento);

                return endereco;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void atualizar(Endereco endereco) {
        String sql = "UPDATE endereco SET endereco_estado=?, endereco_cidade=?, endereco_rua=?, endereco_numero=?, endereco_lotacao=?, evento_evento_id=? WHERE endereco_id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, endereco.getEstado());
            stmt.setString(2, endereco.getCidade());
            stmt.setString(3, endereco.getRua());
            stmt.setString(4, endereco.getNumero());
            stmt.setInt(5, endereco.getLotacao());
            stmt.setInt(6, endereco.getEvento().getId());
            stmt.setInt(7, endereco.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void excluir(int id) {
        String sql = "DELETE FROM endereco WHERE endereco_id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public Endereco buscarPorEventoId(int eventoId) {
        String sql = "SELECT * FROM endereco WHERE evento_evento_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, eventoId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Endereco endereco = new Endereco();
                endereco.setId(rs.getInt("endereco_id"));
                endereco.setEstado(rs.getString("endereco_estado"));
                endereco.setCidade(rs.getString("endereco_cidade"));
                endereco.setRua(rs.getString("endereco_rua"));
                endereco.setNumero(rs.getString("endereco_numero"));
                endereco.setLotacao(rs.getInt("endereco_lotacao"));

                EventoDAO eventoDAO = new EventoDAO();
                Evento evento = eventoDAO.buscarPorId(eventoId);
                endereco.setEvento(evento);

                return endereco;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<Endereco> listar() {
        List<Endereco> enderecos = new ArrayList<>();
        String sql = "SELECT * FROM endereco";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Endereco endereco = new Endereco();
                endereco.setId(rs.getInt("endereco_id"));
                endereco.setEstado(rs.getString("endereco_estado"));
                endereco.setCidade(rs.getString("endereco_cidade"));
                endereco.setRua(rs.getString("endereco_rua"));
                endereco.setNumero(rs.getString("endereco_numero"));
                endereco.setLotacao(rs.getInt("endereco_lotacao"));

                EventoDAO eventoDAO = new EventoDAO();
                Evento evento = eventoDAO.buscarPorId(rs.getInt("evento_evento_id"));
                endereco.setEvento(evento);

                enderecos.add(endereco);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return enderecos;
    }
}
