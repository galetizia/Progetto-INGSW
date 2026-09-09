package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import enums.Ruolo;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthUser {

    private int id;
    private String email;
    private String nome;
    private String cognome;
    private Ruolo ruolo;

    public AuthUser() {}

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}
    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}
    public String getNome() {return nome;}
    public void setNome(String nome) {this.nome = nome;}
    public String getCognome() {return cognome;}
    public void setCognome(String cognome) {this.cognome = cognome;}
    public Ruolo getRuolo() {return ruolo;}
    public void setRuolo(Ruolo ruolo) {this.ruolo = ruolo;}
}
