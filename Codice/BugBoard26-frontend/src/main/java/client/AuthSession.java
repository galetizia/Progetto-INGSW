package client;

import model.AuthUser;

/**
 * Classe che implementa il pattern Singleton per gestire lo stato della sessione utente
 * a livello globale in tutta l'applicazione JavaFX.
 * Mantiene in memoria il Token JWT (necessario per autorizzare le chiamate HTTP al server)
 * e i dati dell'utente attualmente loggato.
 */
public class AuthSession {
    private static AuthSession instance = null;

    private String token;
    private AuthUser utenteCorrente;

    private AuthSession() {}

    /**
     * Restituisce l'unica istanza condivisa della sessione. Se non esiste ancora, la crea.
     *
     * @return L'istanza globale di AuthSession.
     */
    public static AuthSession getInstance() {
        if (instance == null) {
            instance = new AuthSession();
        }
        return instance;
    }

    public void setToken(String token) {
        this.token = token;
    }
    public String getToken() {
        return token;
    }

    public void setUtenteCorrente(AuthUser utente) {this.utenteCorrente = utente;}
    public AuthUser getUtenteCorrente() {return utenteCorrente;}

    /**
     * Pulisce completamente la sessione, eliminando il token e i dati dell'utente.
     * Metodo invocato durante l'operazione di Logout per evitare accessi non autorizzati.
     */
    public void clearSession() {
        this.token = null;
        this.utenteCorrente = null;
    }

    /**
     * Utility per verificare rapidamente se è presente una sessione attiva.
     * Basandosi sulla presenza del token, permette ai Controller di inibire azioni non autorizzate.
     *
     * @return true se un utente è regolarmente loggato, altrimenti false.
     */
    public boolean isLoggedIn() {
        return this.token != null;
    }
}
