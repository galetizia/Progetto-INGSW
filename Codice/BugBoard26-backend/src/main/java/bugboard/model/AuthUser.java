package bugboard.model;

import bugboard.enums.Ruolo;
import jakarta.persistence.*;

@Entity
@Table(name ="auth_user")
@Inheritance(strategy = InheritanceType.JOINED)
public class AuthUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Ruolo ruolo;

    @Column(name = "stato_account", nullable = false)
    private boolean statoAccount = true;

    public AuthUser() {}

    public AuthUser(String password, String email) {
        this.password = password;
        this.email = email;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Ruolo getRuolo() {
        return ruolo;
    }
    public void setRuolo(Ruolo ruolo) {this.ruolo = ruolo;}
    public boolean getStatoAccount() {
        return statoAccount;
    }
    public void setStatoAccount(boolean statoAccount) {
        this.statoAccount = statoAccount;
    }
}
