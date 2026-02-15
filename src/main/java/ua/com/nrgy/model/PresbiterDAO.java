package ua.com.nrgy.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PresbiterDAO {

    public void save(Presbiter p) {
        String sql = "INSERT INTO Presbiterek(nev, nem, szul_ido, szul_hely, utca_id, hazszam, telefonszam, efj_befizetes, beiktatas_eve, megjegyzes) VALUES(?,?,?,?,?,?,?,?,?,?)";

        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, p.getNev());
            pstmt.setString(2, p.getNem());
            pstmt.setString(3, p.getSzul_ido());
            pstmt.setString(4, p.getSzul_hely());
            pstmt.setInt(5, p.getUtca_id());
            pstmt.setString(6, p.getHazszam());
            pstmt.setString(7, p.getTelefonszam());
            pstmt.setInt(8, p.isEfj_befizetes() ? 1 : 0);
            pstmt.setInt(9, p.getBeiktatas_eve());
            pstmt.setString(10, p.getMegjegyzes());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Presbiter> findAll() {
        List<Presbiter> lista = new ArrayList<>();
        String sql = "SELECT p.*, u.nev AS u_nev FROM Presbiterek p LEFT JOIN Utcak u ON p.utca_id = u.id";
        try (Connection conn = DatabaseHandler.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Presbiter p = new Presbiter(
                        rs.getInt("id"),
                        rs.getString("nev"),
                        rs.getString("nem"),
                        rs.getString("szul_ido"),
                        rs.getString("szul_hely"),
                        rs.getInt("utca_id"),
                        rs.getString("hazszam"),
                        rs.getString("telefonszam"),
                        rs.getInt("efj_befizetes") == 1,
                        rs.getInt("beiktatas_eve"),
                        rs.getString("megjegyzes")
                );
                p.setUtcaNeve(rs.getString("u_nev") != null ? rs.getString("u_nev") : "Nincs megadva");
                lista.add(p);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }
}