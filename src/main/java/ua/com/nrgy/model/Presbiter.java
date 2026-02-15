package ua.com.nrgy.model;

import javafx.beans.property.*;

public class Presbiter {
    private final IntegerProperty id;
    private final StringProperty nev;
    private final StringProperty nem; // ÚJ
    private final StringProperty szul_ido;
    private final StringProperty szul_hely;
    private final IntegerProperty utca_id; // ÚJ
    private final StringProperty hazszam; // ÚJ
    private final StringProperty telefonszam;
    private final BooleanProperty efj_befizetes;
    private final IntegerProperty beiktatas_eve;
    private final StringProperty megjegyzes; // ÚJ

    private final StringProperty utcaNeve; // Megjelenítéshez

    public Presbiter(int id, String nev, String nem, String szul_ido, String szul_hely, int utca_id, String hazszam, String telefonszam, boolean efj_befizetes, int beiktatas_eve, String megjegyzes){
        this.id = new SimpleIntegerProperty(id);
        this.nev = new SimpleStringProperty(nev);
        this.nem = new SimpleStringProperty(nem);
        this.szul_ido = new SimpleStringProperty(szul_ido);
        this.szul_hely = new SimpleStringProperty(szul_hely);
        this.utca_id = new SimpleIntegerProperty(utca_id);
        this.hazszam = new SimpleStringProperty(hazszam);
        this.telefonszam = new SimpleStringProperty(telefonszam);
        this.efj_befizetes = new SimpleBooleanProperty(efj_befizetes);
        this.beiktatas_eve = new SimpleIntegerProperty(beiktatas_eve);
        this.megjegyzes = new SimpleStringProperty(megjegyzes);
        this.utcaNeve = new SimpleStringProperty("");
    }

    // Getters & Properties
    public int getId() { return id.get(); }
    public String getNev() { return nev.get(); }
    public String getNem() { return nem.get(); }
    public String getSzul_ido() { return szul_ido.get(); }
    public String getSzul_hely() { return szul_hely.get(); }
    public int getUtca_id() { return utca_id.get(); }
    public String getHazszam() { return hazszam.get(); }
    public String getTelefonszam() { return telefonszam.get(); }
    public boolean isEfj_befizetes() { return efj_befizetes.get(); }
    public int getBeiktatas_eve() { return beiktatas_eve.get(); }
    public String getMegjegyzes() { return megjegyzes.get(); }
    // Add hozzá a Presbiter osztályhoz:

    public String getUtcaNeve() {
        return utcaNeve.get();
    }


    public StringProperty nevProperty() { return nev; }
    public StringProperty nemProperty() { return nem; }
    public StringProperty szul_idoProperty() { return szul_ido; }
    public StringProperty szul_helyProperty() { return szul_hely; }
    public StringProperty utcaNeveProperty() { return utcaNeve; }
    public StringProperty hazszamProperty() { return hazszam; }
    public StringProperty telefonszamProperty() { return telefonszam; }
    public BooleanProperty efj_befizetesProperty() { return efj_befizetes; }
    public IntegerProperty beiktatas_eveProperty() { return beiktatas_eve; }
    public StringProperty megjegyzesProperty() { return megjegyzes; }

    public void setUtcaNeve(String n) { this.utcaNeve.set(n); }

    public int getEletkor() {
        if (szul_ido.get() == null || szul_ido.get().isEmpty()) {
            return 0;
        }
        try {
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy.MM.dd");
            java.time.LocalDate szuletesnap = java.time.LocalDate.parse(szul_ido.get(), formatter);
            java.time.LocalDate ma = java.time.LocalDate.now();
            return java.time.Period.between(szuletesnap, ma).getYears();
        } catch (Exception e) {
            return 0;
        }
    }

    // Kell egy Property is, hogy a TableView meg tudja jeleníteni
    public IntegerProperty eletkorProperty() {
        return new SimpleIntegerProperty(getEletkor());
    }
}