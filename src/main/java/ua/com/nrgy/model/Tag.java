package ua.com.nrgy.model;

import javafx.beans.property.*;

public class Tag {
    private final IntegerProperty id;
    private final StringProperty nev;
    private final StringProperty nem; // ÚJ
    private final StringProperty szul_ido;
    private final StringProperty szul_hely;
    private final IntegerProperty utca_id; // ÚJ
    private final StringProperty hazszam; // ÚJ
    private final StringProperty telefonszam;
    private final BooleanProperty efj_befizetes;
    private final IntegerProperty presbiter_id;
    private final StringProperty megjegyzes; // ÚJ

    // Csak megjelenítéshez (JOIN-ból)
    private final StringProperty presbiterNeve;
    private final StringProperty utcaNeve;

    public Tag(int id, String nev, String nem, String szul_ido, String szul_hely, int utca_id, String hazszam, String telefonszam, boolean efj_befizetes, int presbiter_id, String megjegyzes){
        this.id = new SimpleIntegerProperty(id);
        this.nev = new SimpleStringProperty(nev);
        this.nem = new SimpleStringProperty(nem);
        this.szul_ido = new SimpleStringProperty(szul_ido);
        this.szul_hely = new SimpleStringProperty(szul_hely);
        this.utca_id = new SimpleIntegerProperty(utca_id);
        this.hazszam = new SimpleStringProperty(hazszam);
        this.telefonszam = new SimpleStringProperty(telefonszam);
        this.efj_befizetes = new SimpleBooleanProperty(efj_befizetes);
        this.presbiter_id = new SimpleIntegerProperty(presbiter_id);
        this.megjegyzes = new SimpleStringProperty(megjegyzes);
        this.presbiterNeve = new SimpleStringProperty("");
        this.utcaNeve = new SimpleStringProperty("");
    }

    // Getters for SQL
    public int getId() { return id.get(); }
    public String getNev() { return nev.get(); }
    public String getNem() { return nem.get(); }
    public String getSzul_ido() { return szul_ido.get(); }
    public String getSzul_hely() { return szul_hely.get(); }
    public int getUtca_id() { return utca_id.get(); }
    public String getHazszam() { return hazszam.get(); }
    public String getTelefonszam() { return telefonszam.get(); }
    public boolean isEfj_befizetes() { return efj_befizetes.get(); }
    public int getPresbiter_id() { return presbiter_id.get(); }
    public String getMegjegyzes() { return megjegyzes.get(); }
    public String getUtcaNeve() {
        return utcaNeve.get();
    }

    public String getPresbiterNeve() {
        return presbiterNeve.get();
    }


    // Property-k a TableView-hoz
    public StringProperty nevProperty() { return nev; }
    public StringProperty nemProperty() { return nem; }
    public StringProperty szul_idoProperty() { return szul_ido; }
    public StringProperty szul_helyProperty() { return szul_hely; }
    public StringProperty hazszamProperty() { return hazszam; }
    public StringProperty telefonszamProperty() { return telefonszam; }
    public BooleanProperty efj_befizetesProperty() { return efj_befizetes; }
    public StringProperty megjegyzesProperty() { return megjegyzes; }
    public StringProperty presbiterNeveProperty() { return presbiterNeve; }
    public StringProperty utcaNeveProperty() { return utcaNeve; }

    public void setPresbiterNeve(String n) { this.presbiterNeve.set(n); }
    public void setUtcaNeve(String n) { this.utcaNeve.set(n); }
}