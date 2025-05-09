package dao;

import model.Evento;
import model.Recurso;
import util.Conexao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecursoDAO implements InterfaceDAO<Recurso> {

    private Connection connection;

    public RecursoDAO() {connection = Conexao.getConexao();}

    public void inserir(Recurso recurso) {
        String sql = "INSERT INTO recurso(recursos_nome, recursos_qtd, recursos_descricao, evento_evento_id) VALUES (?,?,?,?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, recurso.getNome());
            stmt.setInt(2, recurso.getQuantidade());
            stmt.setString(3, recurso.getDescricao());
            stmt.setInt(4, recurso.getEvento().getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public Recurso buscarPorId(int id) {
        String sql = "SELECT * FROM recurso WHERE recursos_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Recurso recurso = new Recurso();
                recurso.setId(rs.getInt("recursos_id"));
                recurso.setNome(rs.getString("recursos_nome"));
                recurso.setQuantidade(rs.getInt("recursos_qtd"));
                recurso.setDescricao(rs.getString("recursos_descricao"));

                EventoDAO eventoDAO = new EventoDAO();
                Evento evento = eventoDAO.buscarPorId(rs.getInt("evento_evento_id"));
                recurso.setEvento(evento);

                return recurso;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void atualizar(Recurso recurso) {
        String sql = "UPDATE recurso SET recursos_nome=?, recursos_qtd=?, recursos_descricao=?, evento_evento_id=? WHERE recursos_id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, recurso.getNome());
            stmt.setInt(2, recurso.getQuantidade());
            stmt.setString(3, recurso.getDescricao());
            stmt.setInt(4, recurso.getEvento().getId());
            stmt.setInt(5, recurso.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void excluir(int id) {
        String sql = "DELETE FROM recurso WHERE recursos_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Recurso> listar() {
        List<Recurso> recursos = new ArrayList<>();
        String sql = "SELECT * FROM recurso";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Recurso recurso = new Recurso();
                recurso.setId(rs.getInt("recursos_id"));
                recurso.setNome(rs.getString("recursos_nome"));
                recurso.setQuantidade(rs.getInt("recursos_qtd"));
                recurso.setDescricao(rs.getString("recursos_descricao"));

                EventoDAO eventoDAO = new EventoDAO();
                Evento evento = eventoDAO.buscarPorId(rs.getInt("evento_evento_id"));
                recurso.setEvento(evento);

                recursos.add(recurso);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recursos;
    }

}
