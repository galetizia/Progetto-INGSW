package model;

public class Bug extends Issue {

    public Bug(String titolo, String descrizione) {
        super(titolo, descrizione);
    }

    public Bug(String titolo, String descrizione, String priorita, String urlImmagine) {
        super(titolo, descrizione, priorita, urlImmagine);
    }
}
