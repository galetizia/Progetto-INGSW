package bugboard.service;

import bugboard.model.Issue;
import bugboard.repository.IssueRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IssueService {
    private final IssueRepository issueRepository;

    public IssueService(IssueRepository issueRepository) {
        this.issueRepository = issueRepository;
    }

    public void createIssue(String titolo, String descrizione, String priorita, String urlImmagine) {
        Issue issue = new Issue();

        issue.setTitolo(titolo);
        issue.setDescrizione(descrizione);

        if(priorita!=null && !priorita.isBlank()) issue.setPriorita(priorita);

        if(urlImmagine!=null && !urlImmagine.isBlank()) issue.setUrlImmagine(urlImmagine);


        //stato to-do di default
        issueRepository.save(issue);
    }

    public List<Issue> elencoIssue(){
        List<Issue> issues= issueRepository.findAll();
        System.out.println("ISSUE TROVATE DAL DB: " + issues.size());

        for (Issue issue : issues) {
            System.out.println(
                    "ID: " + issue.getId() +
                            " | Titolo: " + issue.getTitolo()
            );
        }
        return issues;
    }
}
