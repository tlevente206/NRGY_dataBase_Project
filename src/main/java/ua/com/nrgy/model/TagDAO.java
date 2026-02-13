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
        String sql = "SELECT * FROM Tagok";

        try (Connection conn = DatabaseHandler.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                tagok.add(new Tag(
                        rs.getInt("id"),
                        rs.getString("nev"),
                        rs.getString("szul_ido"),
                        rs.getString("szul_hely"),
                        rs.getString("lakcim"),
                        rs.getString("telefonszam"),
                        rs.getInt("efj_befizetes") == 1,
                        rs.getInt("presbiter_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tagok;
    }
}