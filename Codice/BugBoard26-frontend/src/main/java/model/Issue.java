package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import enums.StatoIssue;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Issue {

    private int id;
    private String titolo;
    private String descrizione;
    private String priorita;
    private StatoIssue stato;
    private String tipo;
    private LocalDate data;
    private Attachment allegato;
    private AuthUser assignee;
    private LocalDate dataRisoluzione;


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

    public StatoIssue getStato() {
        return stato;
    }
    public void setStato(StatoIssue stato) {
        this.stato = stato;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Attachment getAllegato(){return allegato;}
    public void setAllegato(Attachment allegato){this.allegato = allegato;}
    public AuthUser getAssignee(){return assignee;}
    public void setAssignee(AuthUser assignee){this.assignee = assignee;}

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    public String getTipo() {return tipo;}

    public LocalDate getData() {
        return data;
    }
    public void setData(LocalDate data) {
        this.data = data;
    }

    public LocalDate getDataRisoluzione() {
        return dataRisoluzione;
    }
    public void setDataRisoluzione(LocalDate dataRisoluzione) {
        this.dataRisoluzione = dataRisoluzione;
    }
}