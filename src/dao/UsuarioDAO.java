package dao;

import model.Usuario;
import model.UsuarioAdmin;
import model.UsuarioComum;
import util.Conexao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO implements InterfaceDAO<Usuario> {

    public UsuarioDAO() {
    }
    public void inserir(Usuario usuario) {
        String sql = "INSERT INTO usuario (usuario_nome, usuario_idade, usuario_cpf, usuario_email, usuario_senha, usuario_tipo) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNome());
            stmt.setInt(2, usuario.getIdade());
            stmt.setString(3, usuario.getCpf());
            stmt.setString(4, usuario.getEmail());
            stmt.setString(5, usuario.getSenha());
            stmt.setString(6, usuario.getTipoUsuario());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir usuário.", e);
        }
    }

    public Usuario buscarPorId(int id) {
        String sql = "SELECT * FROM usuario WHERE usuario_id = ?";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return criarUsuarioAPartirDoResultSet(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuário por ID.", e);
        }
        return null;
    }

    @Override
    public List<Usuario> listar() {
        String sql = "SELECT * FROM usuario";
        List<Usuario> usuarios = new ArrayList<>();

        try (Connection conn = Conexao.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                usuarios.add(criarUsuarioAPartirDoResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar usuários.", e);
        }
        return usuarios;
    }

    @Override
    public void atualizar(Usuario usuario) {
        String sql = "UPDATE usuario SET usuario_nome = ?, usuario_idade = ?, usuario_cpf = ?, usuario_email = ?, usuario_senha = ?, usuario_tipo = ? WHERE usuario_id = ?";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNome());
            stmt.setInt(2, usuario.getIdade());
            stmt.setString(3, usuario.getCpf());
            stmt.setString(4, usuario.getEmail());
            stmt.setString(5, usuario.getSenha());
            stmt.setString(6, usuario.getTipoUsuario());
            stmt.setInt(7, usuario.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar usuário.", e);
        }
    }

    @Override
    public void excluir(int id) {
        String sql = "DELETE FROM usuario WHERE usuario_id = ?";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Usuario autenticar(String email, String senha) {
        String sql = "SELECT * FROM usuario WHERE usuario_email = ? AND usuario_senha = ?";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, senha);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return criarUsuarioAPartirDoResultSet(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro na autenticação.", e);
        }
        return null;
    }

    public void atualizarPerfilAdmin(Usuario usuario) {
        String sql = "UPDATE usuario SET usuario_nome = ?, usuario_email = ?, usuario_tipo = ? WHERE usuario_id = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getTipoUsuario());
            stmt.setInt(4, usuario.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar perfil de admin do usuário.", e);
        }
    }

    private Usuario criarUsuarioAPartirDoResultSet(ResultSet rs) throws SQLException {
        Usuario usuario;
        String tipo = rs.getString("usuario_tipo");

        if ("admin".equalsIgnoreCase(tipo)) {
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
        usuario.setTipoUsuario(tipo);

        return usuario;
    }
}