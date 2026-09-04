package bugboard.model;

import jakarta.persistence.Entity;

@Entity
public class Feature extends Issue {

    public Feature() {}
    public Feature(String titolo, String descrizione) {
        super(titolo, descrizione);
    }

    public Feature(String titolo, String descrizione, String priorita, String urlImmagine) {
        super(titolo, descrizione, priorita, urlImmagine);
    }
}
