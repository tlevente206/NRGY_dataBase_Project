package ua.com.nrgy.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PresbiterDAO {

    // Mentés az adatbázisba
    public void save(Presbiter p) {
        String sql = "INSERT INTO Presbiterek(nev, szul_ido, szul_hely, lakcim, telefonszam, efj_befizetes, beiktatas_eve, utcai) VALUES(?,?,?,?,?,?,?,?)";

        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, p.getNev());
            pstmt.setString(2, p.getSzul_ido());
            pstmt.setString(3, p.getSzul_hely());
            pstmt.setString(4, p.getLakcim());
            pstmt.setString(5, p.getTelefonszam());
            pstmt.setInt(6, p.isEfj_befizetes() ? 1 : 0);
            pstmt.setInt(7, p.getBeiktatas_eve());
            pstmt.setString(8, p.getUtcai());

            pstmt.executeUpdate();
            System.out.println("Presbiter sikeresen elmentve!");
        } catch (SQLException e) {
            System.err.println("Hiba a presbiter mentésekor: " + e.getMessage());
        }
    }

    // Összes presbiter lekérése
    public List<Presbiter> findAll() {
        List<Presbiter> presbiterek = new ArrayList<>();
        String sql = "SELECT * FROM Presbiterek";

        try (Connection conn = DatabaseHandler.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                presbiterek.add(new Presbiter(
                        rs.getInt("id"),
                        rs.getString("nev"),
                        rs.getString("szul_ido"),
                        rs.getString("szul_hely"),
                        rs.getString("lakcim"),
                        rs.getString("telefonszam"),
                        rs.getInt("efj_befizetes") == 1,
                        rs.getInt("beiktatas_eve"),
                        rs.getString("utcai")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return presbiterek;
    }
}