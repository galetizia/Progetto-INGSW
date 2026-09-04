package bugboard.service;

import bugboard.model.InternalUser;
import bugboard.repository.InternalUserRepository;
import org.springframework.stereotype.Service;

@Service
public class InternalUserService {
    private final InternalUserRepository internalUserRepository;

    public InternalUserService(InternalUserRepository internalUserRepository) {
        this.internalUserRepository = internalUserRepository;
    }

    // Creare eventuale metodo

}