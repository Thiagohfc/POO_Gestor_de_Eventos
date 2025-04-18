package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {
    private static final String URL = "jdbc:mysql://localhost:3307/eventosdb?useSSL=false&serverTimezone=UTC";
    private static final String USER = "user";
    private static final String PASS = "user123";

    public static Connection getConexao() {
        try {
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro na conexão com o banco");
        }
    }
}