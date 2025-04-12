package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {
    private static final String URL = "jdbc:mysql://localhost:3306/eventos";
    private static final String USER = "root";
    private static final String PASS = "root";

    public static Connection getConexao() {
        try {
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro na conexão com o banco");
        }
    }
}