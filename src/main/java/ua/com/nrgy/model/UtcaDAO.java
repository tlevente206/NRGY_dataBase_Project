package ua.com.nrgy.model;

import ua.com.nrgy.model.DatabaseHandler;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtcaDAO {
    public List<Utca> findAll() {
        List<Utca> utcak = new ArrayList<>();
        String sql = "SELECT * FROM Utcak ORDER BY nev ASC";
        try (Connection conn = DatabaseHandler.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                utcak.add(new Utca(rs.getInt("id"), rs.getString("nev")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return utcak;
    }
}