package bugboard.service;

import bugboard.enums.StatoIssue;
import bugboard.model.AuthUser;
import bugboard.model.Issue;
import bugboard.repository.IssueRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class IssueService {
    private final IssueRepository issueRepository;

    public IssueService(IssueRepository issueRepository) {
        this.issueRepository = issueRepository;
    }

    public void createIssue(String titolo, String descrizione, String priorita) {
        Issue issue = new Issue();

        issue.setTitolo(titolo);
        issue.setDescrizione(descrizione);

        if(priorita!=null && !priorita.isBlank()) issue.setPriorita(priorita);


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

    public void prendiInCaricoIssue(int issueId, AuthUser sviluppatore) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new RuntimeException("Issue non trovata"));

        // Cambia lo stato e assegna l'utente
        issue.setStato(StatoIssue.ASSEGNATO);
        issue.setAssignee(sviluppatore);

        issueRepository.save(issue);
    }

    // 2. L'utente ha finito e risolve l'issue
    public void risolviIssue(int issueId) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new RuntimeException("Issue non trovata"));

        // Cambia lo stato a RISOLTO
        issue.setStato(StatoIssue.RISOLTO);

        // SALVA LA DATA E L'ORA ESATTA
        issue.setDataRisoluzione(LocalDateTime.now());

        issueRepository.save(issue);
    }
}
