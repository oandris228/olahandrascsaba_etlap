package org.example.etlap;

import java.sql.*;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EtlapService {
    private static final String DB_DRIVER = "mysql";
    private static final String DB_HOST = "localhost";
    private static final String DB_PORT = "3306";
    private static final String DB_DATABASE = "etlapdb";

    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    private final Connection connection;

    public EtlapService() throws SQLException {
        String dbUrl = String.format("jdbc:%s://%s:%s/%s", DB_DRIVER, DB_HOST, DB_PORT, DB_DATABASE);
        connection = DriverManager.getConnection(dbUrl, DB_USER, DB_PASSWORD);
    }

    public List<Etel> getAll() throws SQLException {
        List<Etel> etelek = new ArrayList<>();
        Statement statement = connection.createStatement();
        String sql = "SELECT * FROM etlap";
        ResultSet result = statement.executeQuery(sql);
        while (result.next()) {
            int id = result.getInt("id");
            String name = result.getString("nev");
            String category = result.getString("kategoria");
            int price = result.getInt("ar");
            String description = result.getString("leiras");
            etelek.add(new Etel(id, name, category, price, description));
        }
        return etelek;
    }

    public void create(Etel etel) throws SQLException {
        String sql = "INSERT INTO `etlap` (`id`, `nev`, `leiras`, `ar`, `kategoria`) VALUES (NULL,?,?,?,?);";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, etel.getName());
        statement.setString(4, etel.getCategory());
        statement.setInt(3, etel.getPrice());
        statement.setString(2, etel.getDescription());
        statement.execute();
    }

    public boolean modifyPrice(Etel selectedEtel) throws SQLException {
            String sql = "UPDATE `etlap` SET `ar` = ? WHERE `etlap`.`id` = ?;";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, selectedEtel.getPrice());
            statement.setInt(2, selectedEtel.getId());
            return statement.executeUpdate() == 1;
    }

    public boolean delete(Etel selectedEtel) throws SQLException {
        String sql = "DELETE FROM `etlap` WHERE `etlap`.`id` = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, selectedEtel.getId());
        return statement.executeUpdate() == 1;
    }
}
