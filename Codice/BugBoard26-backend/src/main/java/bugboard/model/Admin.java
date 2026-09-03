package bugboard.model;

import jakarta.persistence.*;

@Entity
@Table(name="admin")
public class Admin extends AuthUser {

    public Admin() {}
    public Admin(String email, String password) {
        super(email, password);
    }
}
