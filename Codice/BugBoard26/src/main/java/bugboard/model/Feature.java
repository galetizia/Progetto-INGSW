package bugboard.model;

public class Feature extends Issue {

    public Feature(String titolo, String descrizione) {
        super(titolo, descrizione);
    }

    public Feature(String titolo, String descrizione, String priorita, String urlImmagine) {
        super(titolo, descrizione, priorita, urlImmagine);
    }
}
