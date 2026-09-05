package model;

import java.time.LocalDate;

public class Issue {

    private int id;
    private String titolo;
    private String descrizione;
    private String priorita;
    private String stato;
    private String tipo;
    private LocalDate data;
    private String urlImmagine;

    public Issue() {}

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

    public String getStato() {
        return stato;
    }
    public void setStato(String stato) {
        this.stato = stato;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUrlImmagine() {
        return urlImmagine;
    }

    public void setUrlImmagine(String urlImmagine) {
        this.urlImmagine = urlImmagine;
    }

    public LocalDate getData() {
        return data;
    }
    public void setData(LocalDate data) {
        this.data = data;
    }
}