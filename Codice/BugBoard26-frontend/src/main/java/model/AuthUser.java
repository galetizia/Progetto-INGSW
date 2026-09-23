package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import enums.Ruolo;

/**
 * Modello dati che rappresenta un utente del sistema.
 * Mappa i dati anagrafici, le credenziali e le statistiche operative calcolate dal backend.
 */
@SuppressWarnings("unused")
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthUser {

    private int id;
    private String email;
    private Ruolo ruolo;
    private boolean statoAccount;
    private int issueAttive;
    private double tempoMedio;
    private int issueRisolte;

    public AuthUser() {}

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}
    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}
    public Ruolo getRuolo() {return ruolo;}
    public void setRuolo(Ruolo ruolo) {this.ruolo = ruolo;}
    public boolean getStatoAccount() {return statoAccount;}
    public void setStatoAccount(boolean statoAccount) {this.statoAccount = statoAccount;}

    public int getIssueAttive() {return issueAttive;}
    public void setIssueAttive(int issueAttive) {this.issueAttive = issueAttive;}
    public double getTempoMedio() {return tempoMedio;}
    public void setTempoMedio(double tempoMedio) {this.tempoMedio = tempoMedio;}
    public int getIssueRisolte() {return issueRisolte;}
    public void setIssueRisolte(int issueRisolte) {this.issueRisolte = issueRisolte;}
}
