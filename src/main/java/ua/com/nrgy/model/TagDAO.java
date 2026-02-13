package ua.com.nrgy.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TagDAO {

    public void save(Tag tag) {
        String sql = "INSERT INTO Tagok(nev, szul_ido, szul_hely, lakcim, telefonszam, efj_befizetes, presbiter_id) VALUES(?,?,?,?,?,?,?)";

        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, tag.getNev());
            pstmt.setString(2, tag.getSzul_ido());
            pstmt.setString(3, tag.getSzul_hely());
            pstmt.setString(4, tag.getLakcim());
            pstmt.setString(5, tag.getTelefonszam());
            pstmt.setInt(6, tag.isEfj_befizetes() ? 1 : 0);
            pstmt.setInt(7, tag.getPresbiter_id());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Tag> findAll() {
        List<Tag> tagok = new ArrayList<>();
        // SQL JOIN: Összekötjük a Tagok és Presbiterek táblát az ID-k alapján
        String sql = "SELECT t.*, p.nev AS p_nev FROM Tagok t " +
                "LEFT JOIN Presbiterek p ON t.presbiter_id = p.id";

        try (Connection conn = DatabaseHandler.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Tag tag = new Tag(
                        rs.getInt("id"),
                        rs.getString("nev"),
                        rs.getString("szul_ido"),
                        rs.getString("szul_hely"),
                        rs.getString("lakcim"),
                        rs.getString("telefonszam"),
                        rs.getInt("efj_befizetes") == 1,
                        rs.getInt("presbiter_id")
                );
                // Itt állítjuk be a nevet, amit a JOIN-ból kaptunk
                tag.setPresbiterNeve(rs.getString("p_nev") != null ? rs.getString("p_nev") : "Nincs kijelölve");
                tagok.add(tag);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tagok;
    }
}