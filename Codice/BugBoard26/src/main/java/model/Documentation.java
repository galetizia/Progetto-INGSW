package model;

public class Documentation extends Issue {

    public Documentation(String titolo, String descrizione) {
        super(titolo, descrizione);
    }

    public Documentation(String titolo, String descrizione, String priorita, String urlImmagine) {
        super(titolo, descrizione, priorita, urlImmagine);
    }
}