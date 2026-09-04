package bugboard.model;

import jakarta.persistence.Entity;

@Entity
public class InternalUser extends AuthUser {

    public InternalUser() {
        super();
    }
}
