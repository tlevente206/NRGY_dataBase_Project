package ua.com.nrgy.model; // Figyelj a package névre, az előzőben model volt, de általában külön database csomagba rakjuk

import java.sql.*;

public class DatabaseHandler {
    private static final String URL = "jdbc:sqlite:gyulekezet.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void initialize() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {

            // 1. Tagok tábla - Teljes szerkezet
            stmt.execute("CREATE TABLE IF NOT EXISTS Tagok ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "nev TEXT NOT NULL,"
                    + "nem TEXT,"              // Férfi/Nő
                    + "szul_ido TEXT,"
                    + "szul_hely TEXT,"
                    + "utca_id INTEGER,"       // Kapcsolat az Utcak táblához
                    + "hazszam TEXT,"
                    + "telefonszam TEXT,"
                    + "efj_befizetes INTEGER,"
                    + "presbiter_id INTEGER,"  // Kapcsolat a Presbiterek táblához
                    + "megjegyzes TEXT)");

            // 2. Presbiterek tábla - Most már a Tagokhoz hasonlóan szétbontva
            stmt.execute("CREATE TABLE IF NOT EXISTS Presbiterek ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "nev TEXT NOT NULL,"
                    + "nem TEXT,"              // ÚJ: Férfi/Nő
                    + "szul_ido TEXT,"
                    + "szul_hely TEXT,"
                    + "utca_id INTEGER,"       // ÚJ: Kapcsolat az Utcak táblához (korábbi lakcim/utcai helyett)
                    + "hazszam TEXT,"          // ÚJ: Házszám külön
                    + "telefonszam TEXT,"
                    + "efj_befizetes INTEGER,"
                    + "beiktatas_eve INTEGER,"
                    + "megjegyzes TEXT)");     // ÚJ: Megjegyzés

            // 3. Utcak tábla
            stmt.execute("CREATE TABLE IF NOT EXISTS Utcak ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "nev TEXT NOT NULL UNIQUE)");

            System.out.println("Adatbázis inicializálva: Tagok és Presbiterek táblák szinkronizálva.");

            feltoltAlapUtckakkal(conn);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void feltoltAlapUtckakkal(Connection conn) {
        String checkSql = "SELECT COUNT(*) FROM Utcak";
        String insertSql = "INSERT OR IGNORE INTO Utcak (nev) VALUES (?)";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(checkSql)) {

            if (rs.next() && rs.getInt(1) == 0) {
                String[] utcak = {
                        "Fő út", "Rákóczi út", "Petőfi út", "Kossuth út",
                        "Arany János út", "Iskola út", "Malom köz",
                        "Temető út", "Újvég", "Alvég", "Hegyi út"
                };

                try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                    for (String utca : utcak) {
                        pstmt.setString(1, utca);
                        pstmt.executeUpdate();
                    }
                    System.out.println("Alapértelmezett utcák feltöltve.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}