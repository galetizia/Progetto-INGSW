package client;

import model.AuthUser;

public class AuthSession {

    private static String token;
    private static AuthUser utenteCorrente;

    public static void setToken(String token) {
        AuthSession.token = token;
    }

    public static String getToken() {
        return token;
    }

    public static void setUtenteCorrente(AuthUser utente) {AuthSession.utenteCorrente = utente;}

    public static AuthUser getUtenteCorrente() {return utenteCorrente;}

    public static void clearToken() {
        token = null;
    }

    public static boolean isLoggedIn() {
        return token != null;
    }
}
