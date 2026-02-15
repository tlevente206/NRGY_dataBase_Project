package ua.com.nrgy.model;

import javafx.beans.property.*;

public class Utca {
    private final IntegerProperty id;
    private final StringProperty nev;

    public Utca(int id, String nev) {
        this.id = new SimpleIntegerProperty(id);
        this.nev = new SimpleStringProperty(nev);
    }

    public int getId() { return id.get(); }
    public String getNev() { return nev.get(); }
    public StringProperty nevProperty() { return nev; }

    @Override
    public String toString() {
        return getNev(); // Ez fontos, hogy a ComboBoxban a név jelenjen meg
    }
}