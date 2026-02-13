package ua.com.nrgy.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseHandler {
    private static final String URL = "jdbc:sqlite:gyulekezet.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void initialize() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {

            // Tagok tábla (ezt már ismerjük)
            stmt.execute("CREATE TABLE IF NOT EXISTS Tagok ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "nev TEXT NOT NULL,"
                    + "szul_ido TEXT, szul_hely TEXT, lakcim TEXT, telefonszam TEXT,"
                    + "efj_befizetes INTEGER, presbiter_id INTEGER)");

            // Presbiterek tábla - az új modelled alapján
            stmt.execute("CREATE TABLE IF NOT EXISTS Presbiterek ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "nev TEXT NOT NULL,"
                    + "szul_ido TEXT,"
                    + "szul_hely TEXT,"
                    + "lakcim TEXT,"
                    + "telefonszam TEXT,"
                    + "efj_befizetes INTEGER,"
                    + "beiktatas_eve INTEGER,"
                    + "utcai TEXT)");

            System.out.println("Adatbázis inicializálva: Tagok és Presbiterek táblák készen állnak.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}