package dao;
import java.util.*;
import model.Evento;
import util.Conexao;
import model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventoDAO {
    private Connection conn;

    public EventoDAO() {
        conn = Conexao.getConexao();
    }

    public void create(Evento evento) throws SQLException {
        String sql = "INSERT INTO evento (evento_titulo, evento_descricao, evento_tipo, evento_data, usuario_usuario_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn =  Conexao.getConexao(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, evento.getTitulo());
            stmt.setString(2, evento.getDescricao());
            stmt.setString(3, evento.getTipo());
            stmt.setDate(4, new java.sql.Date(evento.getData().getTime()));
            stmt.setInt(5, evento.getUsuario().getId());
            stmt.executeUpdate();
        }
    }

    public Evento read(int id) throws SQLException {
        String sql = "SELECT * FROM evento WHERE evento_id = ?";
        try (Connection conn =  Conexao.getConexao(); PreparedStatement stmt = conn.prepareStatement(sql)) {
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
                Usuario usuario = usuarioDAO.read(rs.getInt("usuario_usuario_id"));
                evento.setUsuario(usuario);

                return evento;
            }
        }
        return null;
    }

    public void update(Evento evento) throws SQLException {
        String sql = "UPDATE evento SET evento_titulo=?, evento_descricao=?, evento_tipo=?, evento_data=?, usuario_usuario_id=? WHERE evento_id=?";
        try (Connection conn =  Conexao.getConexao(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, evento.getTitulo());
            stmt.setString(2, evento.getDescricao());
            stmt.setString(3, evento.getTipo());
            stmt.setDate(4, new java.sql.Date(evento.getData().getTime()));
            stmt.setInt(5, evento.getUsuario().getId());
            stmt.setInt(6, evento.getId());
            stmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM evento WHERE evento_id=?";
        try (Connection conn =  Conexao.getConexao(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public List<Evento> listAll() throws SQLException {
        List<Evento> eventos = new ArrayList<>();
        String sql = "SELECT * FROM evento";
        try (Connection conn =  Conexao.getConexao(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Evento evento = new Evento();
                evento.setId(rs.getInt("evento_id"));
                evento.setTitulo(rs.getString("evento_titulo"));
                evento.setDescricao(rs.getString("evento_descricao"));
                evento.setTipo(rs.getString("evento_tipo"));
                evento.setData(rs.getDate("evento_data"));

                UsuarioDAO usuarioDAO = new UsuarioDAO();
                evento.setUsuario(usuarioDAO.read(rs.getInt("usuario_usuario_id")));

                eventos.add(evento);
            }
        }
        return eventos;
    }
}

