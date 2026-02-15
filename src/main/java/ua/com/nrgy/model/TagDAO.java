package ua.com.nrgy.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TagDAO {

    public void save(Tag tag) {
        String sql = "INSERT INTO Tagok(nev, nem, szul_ido, szul_hely, utca_id, hazszam, telefonszam, efj_befizetes, presbiter_id, megjegyzes) VALUES(?,?,?,?,?,?,?,?,?,?)";

        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, tag.getNev());
            pstmt.setString(2, tag.getNem());
            pstmt.setString(3, tag.getSzul_ido());
            pstmt.setString(4, tag.getSzul_hely());
            pstmt.setInt(5, tag.getUtca_id());
            pstmt.setString(6, tag.getHazszam());
            pstmt.setString(7, tag.getTelefonszam());
            pstmt.setInt(8, tag.isEfj_befizetes() ? 1 : 0);
            pstmt.setInt(9, tag.getPresbiter_id());
            pstmt.setString(10, tag.getMegjegyzes());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Tag> findAll() {
        List<Tag> tagok = new ArrayList<>();
        String sql = "SELECT t.*, p.nev AS p_nev, u.nev AS u_nev FROM Tagok t " +
                "LEFT JOIN Presbiterek p ON t.presbiter_id = p.id " +
                "LEFT JOIN Utcak u ON t.utca_id = u.id";

        try (Connection conn = DatabaseHandler.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Tag tag = new Tag(
                        rs.getInt("id"),
                        rs.getString("nev"),
                        rs.getString("nem"),
                        rs.getString("szul_ido"),
                        rs.getString("szul_hely"),
                        rs.getInt("utca_id"),
                        rs.getString("hazszam"),
                        rs.getString("telefonszam"),
                        rs.getInt("efj_befizetes") == 1,
                        rs.getInt("presbiter_id"),
                        rs.getString("megjegyzes")
                );
                tag.setPresbiterNeve(rs.getString("p_nev") != null ? rs.getString("p_nev") : "Nincs");
                tag.setUtcaNeve(rs.getString("u_nev") != null ? rs.getString("u_nev") : "Nincs megadva");
                tagok.add(tag);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return tagok;
    }

    public void delete(int id) {
        String sql = "DELETE FROM Tagok WHERE id = ?";

        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Tag törölve, ID: " + id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Tag tag) {
        String sql = "UPDATE Tagok SET nev=?, nem=?, szul_ido=?, szul_hely=?, utca_id=?, hazszam=?, telefonszam=?, efj_befizetes=?, presbiter_id=?, megjegyzes=? WHERE id=?";

        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, tag.getNev());
            pstmt.setString(2, tag.getNem());
            pstmt.setString(3, tag.getSzul_ido());
            pstmt.setString(4, tag.getSzul_hely());
            pstmt.setInt(5, tag.getUtca_id());
            pstmt.setString(6, tag.getHazszam());
            pstmt.setString(7, tag.getTelefonszam());
            pstmt.setInt(8, tag.isEfj_befizetes() ? 1 : 0);
            pstmt.setInt(9, tag.getPresbiter_id());
            pstmt.setString(10, tag.getMegjegyzes());
            pstmt.setInt(11, tag.getId());

            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}