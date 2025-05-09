package dao;

import model.Atracao;
import model.Evento;
import util.Conexao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AtracaoDAO implements InterfaceDAO<Atracao> {

    private Connection connection;

    public AtracaoDAO() {connection = Conexao.getConexao();}

    public void inserir(Atracao atracao) {
        String sql = "INSERT INTO atracao(atracoes_nome, atracoes_tipo, atracoes_horario, evento_evento_id) VALUES (?,?,?,?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, atracao.getNome());
            stmt.setString(2, atracao.getTipo());
            stmt.setString(3, atracao.getHorario());
            stmt.setInt(4, atracao.getEvento().getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public Atracao buscarPorId(int id) {
        String sql = "SELECT * FROM atracao WHERE atracoes_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Atracao atracao = new Atracao();
                atracao.setId(rs.getInt("atracoes_id"));
                atracao.setNome(rs.getString("atracoes_nome"));
                atracao.setTipo(rs.getString("atracoes_tipo"));

                EventoDAO eventoDAO = new EventoDAO();
                Evento evento = eventoDAO.buscarPorId(rs.getInt("evento_evento_id"));
                atracao.setEvento(evento);

                return atracao;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void atualizar(Atracao atracao) {
        String sql = "UPDATE atracao SET atracoes_nome=?, atracoes_tipo=?, atracoes_horario=?, evento_evento_id=? WHERE atracoes_id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, atracao.getNome());
            stmt.setString(2, atracao.getTipo());
            stmt.setString(3, atracao.getHorario());
            stmt.setInt(4, atracao.getEvento().getId());
            stmt.setInt(5, atracao.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void excluir(int id) {
        String sql = "DELETE FROM atracao WHERE atracoes_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Atracao> listar() {
        List<Atracao> atracoes = new ArrayList<>();
        String sql = "SELECT * FROM atracao";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Atracao atracao = new Atracao();
                atracao.setId(rs.getInt("atracoes_id"));
                atracao.setNome(rs.getString("atracoes_nome"));
                atracao.setTipo(rs.getString("atracoes_tipo"));
                atracao.setHorario(rs.getString("atracoes_horario"));

                EventoDAO eventoDAO = new EventoDAO();
                Evento evento = eventoDAO.buscarPorId(rs.getInt("evento_evento_id"));
                atracao.setEvento(evento);

                atracoes.add(atracao);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return atracoes;
    }

}
