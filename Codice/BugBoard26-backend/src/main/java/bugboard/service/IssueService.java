package bugboard.service;

import bugboard.enums.StatoIssue;
import bugboard.enums.TipoIssue;
import bugboard.model.Attachment;
import bugboard.model.AuthUser;
import bugboard.model.Issue;
import bugboard.repository.AuthUserRepository;
import bugboard.repository.IssueRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class IssueService {
    private final IssueRepository issueRepository;
    private final AuthUserRepository authUserRepository;

    public IssueService(IssueRepository issueRepository, AuthUserRepository authUserRepository) {
        this.issueRepository = issueRepository;
        this.authUserRepository = authUserRepository;
    }

    public void createIssue(String titolo, String descrizione, TipoIssue tipologia , String priorita, MultipartFile file) {
        Issue issue = new Issue();

        issue.setTitolo(titolo);
        issue.setDescrizione(descrizione);
        issue.setTipo(tipologia);

        if(priorita!=null && !priorita.isBlank()) issue.setPriorita(priorita);

        issue.setStato(StatoIssue.TO_DO);

        if(file!=null && !file.isEmpty()){
            try{
                Attachment allegato = new Attachment();
                allegato.setNome(file.getOriginalFilename());
                allegato.setTipo(file.getContentType());
                allegato.setContenuto(file.getBytes());

                issue.setAllegato(allegato);
            } catch(IOException e){
                throw new RuntimeException("Errore nel caricamento dell'immagine", e);
            }
        }
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

    public Map<String, Integer> countIssueStates() {
        List<Object[]> issueStates = issueRepository.findAllIssueStates();
        Map<String, Integer> map = new HashMap<>();
        for (Object[] obj : issueStates) {
            String tipo = obj[0].toString();
            Integer count = ((Number) obj[1]).intValue();
            map.put(tipo, count);
        }
        return map;
    }

    public Map<String, Integer> countIssueTypes() {
        List<Object[]> issueTypes = issueRepository.findAllIssueTypes();
        Map<String, Integer> map = new HashMap<>();
        for (Object[] obj : issueTypes) {
            String tipo = obj[0].toString();
            Integer count = ((Number) obj[1]).intValue();
            map.put(tipo, count);
        }
        return map;
    }

    public boolean assegnaIssueUtente(int issueId, String emailUser) {
        try{
            Issue issue = issueRepository.findById(issueId)
                    .orElseThrow(() -> new RuntimeException("Issue non trovata"));

            AuthUser user = authUserRepository.findByEmail(emailUser).orElseThrow(() -> new RuntimeException("Utente non trovato"));

            // Cambia lo stato e assegna l'utente
            issue.setStato(StatoIssue.ASSEGNATO);
            issue.setAssignee(user);

            issueRepository.save(issue);
            return true;
        } catch(Exception e){
            e.printStackTrace();
            return false;
        }

    }

    // 2. L'utente ha finito e risolve l'issue
    public void risolviIssue(int issueId) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new RuntimeException("Issue non trovata"));

        // Cambia lo stato a: RISOLTO
        issue.setStato(StatoIssue.RISOLTO);

        // SALVA LA DATA E L'ORA ESATTA
        issue.setDataRisoluzione(LocalDateTime.now());

        issueRepository.save(issue);
    }

    // 1. Recupera i bug per la tabella delle Issue
    public List<Issue> getIssueAttive() {
        return issueRepository.findByStatoIn(List.of(StatoIssue.TO_DO, StatoIssue.ASSEGNATO));
    }

    // 2. Recupera i bug per la tabella Archivio Bug
    public List<Issue> getIssueArchiviate() {
        return issueRepository.findByStatoIn(List.of(StatoIssue.RISOLTO, StatoIssue.ARCHIVIATO));
    }

    // 3. Il metodo per l'Admin che archivia un bug
    public Issue archiviaIssue(int idIssue) {
        Issue issue = issueRepository.findById(idIssue)
                .orElseThrow(() -> new IllegalArgumentException("Issue non trovata con ID: " + idIssue));

        issue.setStato(StatoIssue.ARCHIVIATO);
        issue.setDataRisoluzione(LocalDateTime.now());

        return issueRepository.save(issue);
    }

    public boolean eliminaIssue(int idIssue) {
        if(issueRepository.existsById(idIssue)) {
            issueRepository.deleteById(idIssue);
            return true;
        }
        return false;
    }
}
