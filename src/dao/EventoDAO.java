package dao;

import model.Evento;
import model.Usuario;
import util.Conexao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventoDAO {
    private Connection conn;

    public EventoDAO() {
        conn = Conexao.getConexao();
    }

    public void inserir(Evento evento) {
        String sql = "INSERT INTO evento (evento_titulo, evento_descricao, evento_tipo, evento_data, usuario_usuario_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, evento.getTitulo());
            stmt.setString(2, evento.getDescricao());
            stmt.setString(3, evento.getTipo());
            stmt.setDate(4, new java.sql.Date(evento.getData().getTime()));
            stmt.setInt(5, evento.getUsuario().getId());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                evento.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Evento buscarPorId(int id) {
        String sql = "SELECT * FROM evento WHERE evento_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Evento evento = new Evento();
                evento.setId(rs.getInt("evento_id"));
                evento.setTitulo(rs.getString("evento_titulo"));
                evento.setDescricao(rs.getString("evento_descricao"));
                evento.setTipo(rs.getString("evento_tipo"));
                evento.setData(rs.getDate("evento_data"));

                UsuarioDAO usuarioDAO = new UsuarioDAO();
                Usuario usuario = usuarioDAO.buscarPorId(rs.getInt("usuario_usuario_id"));
                evento.setUsuario(usuario);

                return evento;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void atualizar(Evento evento) {
        String sql = "UPDATE evento SET evento_titulo=?, evento_descricao=?, evento_tipo=?, evento_data=?, usuario_usuario_id=? WHERE evento_id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, evento.getTitulo());
            stmt.setString(2, evento.getDescricao());
            stmt.setString(3, evento.getTipo());
            stmt.setDate(4, new java.sql.Date(evento.getData().getTime()));
            stmt.setInt(5, evento.getUsuario().getId());
            stmt.setInt(6, evento.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void excluir(int id) {
        String sql = "DELETE FROM evento WHERE evento_id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Evento> listar() {
        List<Evento> eventos = new ArrayList<>();
        String sql = "SELECT * FROM evento";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Evento evento = new Evento();
                evento.setId(rs.getInt("evento_id"));
                evento.setTitulo(rs.getString("evento_titulo"));
                evento.setDescricao(rs.getString("evento_descricao"));
                evento.setTipo(rs.getString("evento_tipo"));
                evento.setData(rs.getDate("evento_data"));

                UsuarioDAO usuarioDAO = new UsuarioDAO();
                Usuario usuario = usuarioDAO.buscarPorId(rs.getInt("usuario_usuario_id"));
                evento.setUsuario(usuario);

                eventos.add(evento);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return eventos;
    }
}
