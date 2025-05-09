package dao;

import model.Usuario;
import model.UsuarioAdmin;
import model.UsuarioComum;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO implements InterfaceDAO<Usuario> {
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
            statement.setString(6, "comum"); // FORÇA o tipo a ser "comum"
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
                String tipo = rs.getString("usuario_tipo"); // primeiro pegar o tipo
                Usuario usuario;

                if (tipo.equalsIgnoreCase("admin")) {
                    usuario = new UsuarioAdmin();
                } else {
                    usuario = new UsuarioComum(); // padrão: comum
                }

                usuario.setId(rs.getInt("usuario_id"));
                usuario.setNome(rs.getString("usuario_nome"));
                usuario.setIdade(rs.getInt("usuario_idade"));
                usuario.setCpf(rs.getString("usuario_cpf"));
                usuario.setEmail(rs.getString("usuario_email"));
                usuario.setSenha(rs.getString("usuario_senha"));

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
                String tipo = resultSet.getString("usuario_tipo");
                Usuario usuario;

                if (tipo.equalsIgnoreCase("admin")) {
                    usuario = new UsuarioAdmin();
                } else {
                    usuario = new UsuarioComum(); // padrão: comum
                }

                usuario.setId(resultSet.getInt("usuario_id"));
                usuario.setNome(resultSet.getString("usuario_nome"));
                usuario.setIdade(resultSet.getInt("usuario_idade"));
                usuario.setCpf(resultSet.getString("usuario_cpf"));
                usuario.setEmail(resultSet.getString("usuario_email"));
                usuario.setSenha(resultSet.getString("usuario_senha"));

                usuarios.add(usuario);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return usuarios;
    }

    public Usuario autenticar(String email, String senha) {
            try {
                String sql = "SELECT * FROM usuario WHERE usuario_email = ? AND usuario_senha = ?";
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setString(1, email);
                stmt.setString(2, senha);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    String tipo = rs.getString("usuario_tipo");
                    Usuario usuario;
                    if (tipo.equalsIgnoreCase("admin")) {
                        usuario = new UsuarioAdmin();
                    } else {
                        usuario = new UsuarioComum();
                    }

                    usuario.setId(rs.getInt("usuario_id"));
                    usuario.setNome(rs.getString("usuario_nome"));
                    usuario.setIdade(rs.getInt("usuario_idade"));
                    usuario.setCpf(rs.getString("usuario_cpf"));
                    usuario.setEmail(rs.getString("usuario_email"));
                    usuario.setSenha(rs.getString("usuario_senha"));

                    return usuario;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

    public void atualizar(Usuario usuario) {
        try {
            String sql = "UPDATE usuario SET usuario_nome = ?, usuario_idade = ?, usuario_cpf = ?, usuario_email = ?, usuario_senha = ?, usuario_tipo = ? WHERE usuario_id = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, usuario.getNome());
            stmt.setInt(2, usuario.getIdade());
            stmt.setString(3, usuario.getCpf());
            stmt.setString(4, usuario.getEmail());
            stmt.setString(5, usuario.getSenha());
            stmt.setString(6, usuario.getTipoUsuario());
            stmt.setInt(7, usuario.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void excluir(int id) {
        try {
            String sql = "DELETE FROM usuario WHERE usuario_id = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
