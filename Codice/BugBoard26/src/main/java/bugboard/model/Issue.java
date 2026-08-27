package bugboard.model;

public class Issue {

    private String titolo;
    private String descrizione;
    private String priorita;
    private String urlImmagine;
    private String stato = "todo";

    public Issue(String titolo, String descrizione) {
        this.titolo = titolo;
        this.descrizione = descrizione;
    }

    public Issue(String titolo, String descrizione, String priorita, String urlImmagine) {
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.priorita = priorita;
        this.urlImmagine = urlImmagine;
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
}
