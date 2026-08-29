package bugboard.service;

import bugboard.model.Issue;
import bugboard.repository.IssueRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class IssueService {
    private final IssueRepository issueRepository;

    public IssueService(IssueRepository issueRepository) {
        this.issueRepository = issueRepository;
    }

    public Issue createIssue(String titolo, String descrizione, String priorita, String urlImmagine) {
        Issue issue = new Issue();

        issue.setTitolo(titolo);
        issue.setDescrizione(descrizione);

        if(priorita!=null && !priorita.isBlank()) issue.setPriorita(priorita);

        if(urlImmagine!=null && !urlImmagine.isBlank()) issue.setUrlImmagine(urlImmagine);


        //stato to-do di default
        return issueRepository.save(issue);
    }
}
