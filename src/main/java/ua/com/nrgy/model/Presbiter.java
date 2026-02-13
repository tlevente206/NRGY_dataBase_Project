package ua.com.nrgy.model;

import javafx.beans.property.*;

public class Presbiter {
    private final IntegerProperty id;
    private final StringProperty nev;
    private final StringProperty szul_ido;
    private final StringProperty szul_hely;
    private final StringProperty lakcim;
    private final StringProperty telefonszam;
    private final BooleanProperty efj_befizetes;
    private final IntegerProperty beiktatas_eve;
    private final StringProperty utcai;

    public Presbiter(int id, String nev, String szul_ido, String szul_hely, String lakcim, String telefonszam, boolean efj_befizetes, int beiktatas_eve, String utcai){
        this.id = new SimpleIntegerProperty(id);
        this.nev = new SimpleStringProperty(nev);
        this.szul_ido = new SimpleStringProperty(szul_ido);
        this.szul_hely = new SimpleStringProperty(szul_hely);
        this.lakcim = new SimpleStringProperty(lakcim);
        this.telefonszam = new SimpleStringProperty(telefonszam);
        this.efj_befizetes = new SimpleBooleanProperty(efj_befizetes);
        this.beiktatas_eve = new SimpleIntegerProperty(beiktatas_eve);
        this.utcai = new SimpleStringProperty(utcai);
    }

    // --- Ezek kellenek az SQL mentéshez (Sima értékek) ---
    public int getId() { return id.get(); }
    public String getNev() { return nev.get(); }
    public String getSzul_ido() { return szul_ido.get(); }
    public String getSzul_hely() { return szul_hely.get(); }
    public String getLakcim() { return lakcim.get(); }
    public String getTelefonszam() { return telefonszam.get(); }
    public boolean isEfj_befizetes() { return efj_befizetes.get(); }
    public int getBeiktatas_eve() { return beiktatas_eve.get(); }
    public String getUtcai() { return utcai.get(); }

    // --- Ezek kellenek a JavaFX táblázathoz (Property-k) ---
    public IntegerProperty idProperty() { return id; }
    public StringProperty nevProperty() { return nev; }
    public StringProperty szul_idoProperty() { return szul_ido; }
    public StringProperty szul_helyProperty() { return szul_hely; }
    public StringProperty lakcimProperty() { return lakcim; }
    public StringProperty telefonszamProperty() { return telefonszam; }
    public BooleanProperty efj_befizetesProperty() { return efj_befizetes; }
    public IntegerProperty beiktatas_eveProperty() { return beiktatas_eve; }
    public StringProperty utcaiProperty() { return utcai; }
}
