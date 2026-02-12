package ua.com.nrgy.model;

import java.sql.*;

public class DatabaseHandler {
    // Ez határozza meg, hol legyen a fájl.
    // A "jdbc:sqlite:" a protokoll, a "gyulekezet.db" a fájlnév.
    private static final String URL = "jdbc:sqlite:gyulekezet.db";

    // Ezt a metódust hívjuk meg minden műveletnél (mentés, törlés, lekérés)
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void initialize() {
        // SQL parancs a tábla létrehozásához az új mezőkkel
        // Az "IF NOT EXISTS" megelőzi, hogy minden indításnál hiba legyen
        String createTableSql = "CREATE TABLE IF NOT EXISTS Tagok ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "nev TEXT NOT NULL,"
                + "szul_ido TEXT,"
                + "szul_hely TEXT,"
                + "lakcim TEXT,"
                + "telefonszam TEXT,"
                + "efj_befizetes INTEGER," // SQLite-ban nincs Boolean, 0 vagy 1-et tárolunk
                + "presbiter_neve TEXT"
                + ");";

        // Try-with-resources blokk: automatikusan lezárja a kapcsolatot, ha végzett
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createTableSql);
            System.out.println("Adatbázis és Tagok tábla sikeresen inicializálva.");

        } catch (SQLException e) {
            System.err.println("Hiba az adatbázis indításakor: " + e.getMessage());
        }
    }

    public static void addTag(Tag tag) {
        String sql = "INSERT INTO Tagok(nev, szul_ido, szul_hely, lakcim, telefonszam, efj_befizetes, presbiter_neve) VALUES(?,?,?,?,?,?,?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // A .getNev() már Stringet ad vissza, nem Property-t!
            pstmt.setString(1, tag.getNev());
            pstmt.setString(2, tag.getSzul_ido());
            pstmt.setString(3, tag.getSzul_hely());
            pstmt.setString(4, tag.getLakcim());
            pstmt.setString(5, tag.getTelefonszam());

            // Boolean -> SQLite Integer (1 vagy 0)
            pstmt.setInt(6, tag.isEfj_befizetes() ? 1 : 0);

            // Mivel presbiter_id-t használsz, ez most már setInt!
            pstmt.setInt(7, tag.getPresbiter_id());

            pstmt.executeUpdate();
            System.out.println("Sikeres mentés!");

        } catch (SQLException e) {
            System.out.println("Hiba a mentés során: " + e.getMessage());
        }
    }

    public static void getAllTags() {
        String sql = "SELECT * FROM Tagok";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             // A ResultSet tartalmazza az adatbázisból visszakapott sorokat
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("--- Gyülekezeti tagok listája ---");

            // Addig megyünk, amíg van következő sor
            while (rs.next()) {
                int id = rs.getInt("id");
                String nev = rs.getString("nev");
                String lakcim = rs.getString("lakcim");
                int efj = rs.getInt("efj_befizetes");

                // Kiírjuk szépen formázva a konzolra
                System.out.printf("ID: %d | Név: %s | Lakcím: %s | EFJ befizetve: %s%n",
                        id, nev, lakcim, (efj == 1 ? "Igen" : "Nem"));
            }
            System.out.println("---------------------------------");

        } catch (SQLException e) {
            System.out.println("Hiba a lekérdezés során: " + e.getMessage());
        }
    }
}