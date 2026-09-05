package bugboard.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "issue")
@Inheritance(strategy = InheritanceType.JOINED)
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String titolo;
    @Column(nullable = false)
    private String descrizione;
    @Column
    private String priorita = "no";
    @Column
    private String urlImmagine = "no";
    @Column(nullable = false)
    private String stato = "todo";
    @Column(nullable = false)
    private String tipo;
    @Column
    private LocalDate data;

    public Issue() {}

    public Issue(String titolo, String descrizione) {
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.data = LocalDate.now();
    }

    public Issue(String titolo, String descrizione, String priorita, String urlImmagine) {
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.priorita = priorita;
        this.urlImmagine = urlImmagine;
        this.data = LocalDate.now();
    }

    public String getTitolo() {
        return titolo;
    }
    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }
    public String getDescrizione() {
        return descrizione;
    }
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }
    public String getPriorita() {
        return priorita;
    }
    public void setPriorita(String priorita) {
        this.priorita = priorita;
    }
    public String getUrlImmagine() {
        return urlImmagine;
    }
    public void setUrlImmagine(String urlImmagine) {
        this.urlImmagine = urlImmagine;
    }

    public String getStato() {
        return stato;
    }
    public void setStato(String stato) {
        this.stato = stato;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
}
