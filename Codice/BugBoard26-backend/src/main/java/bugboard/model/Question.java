package bugboard.model;

public class Question extends Issue {

    public Question(String titolo, String descrizione) {
        super(titolo, descrizione);
    }

    public Question(String titolo, String descrizione, String priorita, String urlImmagine) {
        super(titolo, descrizione, priorita, urlImmagine);
    }
}
