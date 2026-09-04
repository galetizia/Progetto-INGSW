package bugboard.model;

import jakarta.persistence.Entity;

@Entity
public class Question extends Issue {

    public Question() {}
    public Question(String titolo, String descrizione) {
        super(titolo, descrizione);
    }

    public Question(String titolo, String descrizione, String priorita, String urlImmagine) {
        super(titolo, descrizione, priorita, urlImmagine);
    }
}
