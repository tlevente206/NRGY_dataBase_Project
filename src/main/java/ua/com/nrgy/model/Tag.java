package ua.com.nrgy.model;

import javafx.beans.property.*;

public class Tag {
    private final IntegerProperty id;
    private final StringProperty nev;
    private final StringProperty szul_ido;
    private final StringProperty szul_hely;
    private final StringProperty lakcim;
    private final StringProperty telefonszam;
    private final BooleanProperty efj_befizetes;
    private final IntegerProperty presbiter_id;

    public Tag(int id, String nev, String szul_ido, String szul_hely, String lakcim, String telefonszam, boolean efj_befizetes, int presbiter_id){
        this.id = new SimpleIntegerProperty(id);
        this.nev = new SimpleStringProperty(nev);
        this.szul_ido = new SimpleStringProperty(szul_ido);
        this.szul_hely = new SimpleStringProperty(szul_hely);
        this.lakcim = new SimpleStringProperty(lakcim);
        this.telefonszam = new SimpleStringProperty(telefonszam);
        this.efj_befizetes = new SimpleBooleanProperty(efj_befizetes);
        this.presbiter_id = new SimpleIntegerProperty(presbiter_id);
    }

    // --- Ezek kellenek az SQL mentéshez (Sima értékek) ---
    public int getId() { return id.get(); }
    public String getNev() { return nev.get(); }
    public String getSzul_ido() { return szul_ido.get(); }
    public String getSzul_hely() { return szul_hely.get(); }
    public String getLakcim() { return lakcim.get(); }
    public String getTelefonszam() { return telefonszam.get(); }
    public boolean isEfj_befizetes() { return efj_befizetes.get(); }
    public int getPresbiter_id() { return presbiter_id.get(); }

    // --- Ezek kellenek a JavaFX táblázathoz (Property-k) ---
    public IntegerProperty idProperty() { return id; }
    public StringProperty nevProperty() { return nev; }
    public StringProperty szul_idoProperty() { return szul_ido; }
    public StringProperty szul_helyProperty() { return szul_hely; }
    public StringProperty lakcimProperty() { return lakcim; }
    public StringProperty telefonszamProperty() { return telefonszam; }
    public BooleanProperty efj_befizetesProperty() { return efj_befizetes; }
    public IntegerProperty presbiter_idProperty() { return presbiter_id; }
}