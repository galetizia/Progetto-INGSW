package bugboard.model;

public class AuthUser {
    private String password;
    private String email;

    public AuthUser() {}

    public AuthUser(String password, String email) {
        this.password = password;
        this.email = email;
    }
}
