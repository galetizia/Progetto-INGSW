package client;

import model.AuthUser;

public class AuthSession {
    private static AuthSession instance = null;

    private String token;
    private AuthUser utenteCorrente;

    private AuthSession() {}

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

    public void clearSession() {
        this.token = null;
        this.utenteCorrente = null;
    }

    public boolean isLoggedIn() {
        return this.token != null;
    }
}
