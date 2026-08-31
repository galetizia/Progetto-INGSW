package bugboard.service;

import org.springframework.stereotype.Service;
import bugboard.repository.ExternalUserRepository;

@Service
public class ExternalUserService {
    private final ExternalUserRepository externalUserRepository;

    public ExternalUserService(ExternalUserRepository externalUserRepository) {
        this.externalUserRepository = externalUserRepository;
    }

}
