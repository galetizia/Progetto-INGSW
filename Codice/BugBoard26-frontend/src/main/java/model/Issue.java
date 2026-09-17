package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import enums.StatoIssue;
import enums.TipoIssue;
import java.time.LocalDateTime;

@SuppressWarnings("unused")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Issue {

    private int id;
    private String titolo;
    private String descrizione;
    private String priorita;
    private StatoIssue stato;
    private TipoIssue tipo;
    private LocalDateTime data;
    private Attachment allegato;
    private AuthUser assignee;
    private LocalDateTime dataRisoluzione;
    private LocalDateTime dataAssegnazione;

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
        return stato.name();
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

    public void setTipo(TipoIssue tipo) {
        this.tipo = tipo;
    }
    public TipoIssue getTipo() {return tipo;}

    public LocalDateTime getData() {
        return data;
    }
    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public LocalDateTime getDataRisoluzione() {
        return dataRisoluzione;
    }
    public void setDataRisoluzione(LocalDateTime dataRisoluzione) {
        this.dataRisoluzione = dataRisoluzione;
    }

    public LocalDateTime getDataAssegnazione() {
        return dataAssegnazione;
    }
    public void setDataAssegnazione(LocalDateTime dataAssegnazione) {
        this.dataAssegnazione = dataAssegnazione;
    }
}