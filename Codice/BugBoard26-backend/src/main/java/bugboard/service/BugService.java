package bugboard.service;

import bugboard.model.Bug;
import bugboard.repository.BugRepository;
import org.springframework.stereotype.Service;

@Service
public class BugService {
    private final BugRepository bugRepository;

    public BugService(BugRepository bugRepository) {
        this.bugRepository = bugRepository;
    }

    // Creare eventuale metodo

}