package client;

public class AuthSession {

    private static String token;

    public static void setToken(String token) {
        AuthSession.token = token;
    }

    public static String getToken() {
        return token;
    }

    public static void clearToken() {
        token = null;
    }

    public static boolean isLoggedIn() {
        return token != null;
    }
}
