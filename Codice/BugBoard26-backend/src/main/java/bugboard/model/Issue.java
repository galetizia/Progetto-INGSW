package bugboard.model;

import bugboard.enums.StatoIssue;
import bugboard.enums.TipoIssue;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;


/**
 * Entità che rappresenta una issue all'interno del sistema e ne traccia l'intero ciclo di vita.
 */
@SuppressWarnings("unused")
@Entity
@Table(name = "issue")
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

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatoIssue stato = StatoIssue.TO_DO;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoIssue tipo;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime data;

    @Column(name = "data_risoluzione")
    private LocalDateTime dataRisoluzione;

    @ManyToOne
    @JoinColumn(name = "assignee_id")
    private AuthUser assignee;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_allegato")
    private Attachment allegato;

    @Column(name = "data_assegnazione")
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
    public void setPriorita(String priorita) {this.priorita = priorita;}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDateTime getDataRisoluzione() {return dataRisoluzione;}
    public void setDataRisoluzione(LocalDateTime dataRisoluzione) {this.dataRisoluzione = dataRisoluzione;}

    public AuthUser getAssignee() {return assignee;}
    public void setAssignee(AuthUser assignee) {this.assignee = assignee;}

    public StatoIssue getStato() {return this.stato;}
    public void setStato(StatoIssue stato) {this.stato = stato;}

    public Attachment getAllegato() {return allegato;}
    public void setAllegato(Attachment allegato) {this.allegato = allegato;}

    public void setTipo(TipoIssue tipo) {this.tipo = tipo;}
    public TipoIssue getTipo() {return tipo;}

    public LocalDateTime getData() {
        return data;
    }
    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public LocalDateTime getDataAssegnazione() {
        return dataAssegnazione;
    }
    public void setDataAssegnazione(LocalDateTime dataAssegnazione) {
        this.dataAssegnazione = dataAssegnazione;
    }

}
