package bugboard.model;

import jakarta.persistence.*;

@Entity
@Table(name="external_user")
public class ExternalUser extends AuthUser {

    public ExternalUser() {
        super();
    }
}
