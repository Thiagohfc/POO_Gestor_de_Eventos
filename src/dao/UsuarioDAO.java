package dao;

import model.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {
    private Connection connection;

    public UsuarioDAO() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3307/eventosdb", "user", "user123");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void inserir(Usuario usuario) {
        try {
            String sql = "INSERT INTO usuario (usuario_nome, usuario_idade, usuario_cpf, usuario_email, usuario_senha, usuario_tipo) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, usuario.getNome());
            statement.setInt(2, usuario.getIdade());
            statement.setString(3, usuario.getCpf());
            statement.setString(4, usuario.getEmail());
            statement.setString(5, usuario.getSenha());
            statement.setString(6, usuario.getTipoUsuario());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Usuario buscarPorId(int id) {
        try {
            String sql = "SELECT * FROM usuario WHERE usuario_id = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setId(rs.getInt("usuario_id"));
                usuario.setNome(rs.getString("usuario_nome"));
                usuario.setIdade(rs.getInt("usuario_idade"));
                usuario.setCpf(rs.getString("usuario_cpf"));
                usuario.setEmail(rs.getString("usuario_email"));
                usuario.setSenha(rs.getString("usuario_senha"));
                usuario.setTipoUsuario(rs.getString("usuario_tipo"));
                return usuario;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Usuario> listar() {
        List<Usuario> usuarios = new ArrayList<>();
        try {
            String sql = "SELECT * FROM usuario";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                Usuario usuario = new Usuario();
                usuario.setId(resultSet.getInt("usuario_id"));
                usuario.setNome(resultSet.getString("usuario_nome"));
                usuario.setIdade(resultSet.getInt("usuario_idade"));
                usuario.setCpf(resultSet.getString("usuario_cpf"));
                usuario.setEmail(resultSet.getString("usuario_email"));
                usuario.setSenha(resultSet.getString("usuario_senha"));
                usuario.setTipoUsuario(resultSet.getString("usuario_tipo"));
                usuarios.add(usuario);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuarios;
    }
}
