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
        issueRepository.save(issue);
    }

    public List<Issue> elencoIssue(){
        return issueRepository.findAll();
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

            issue.setStato(StatoIssue.ASSEGNATO);
            issue.setAssignee(user);
            issue.setDataAssegnazione(LocalDateTime.now());

            issueRepository.save(issue);
            return true;
        } catch(Exception e){
            return false;
        }

    }

    public void risolviIssue(int issueId) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new RuntimeException("Issue non trovata"));

        issue.setStato(StatoIssue.RISOLTO);

        issue.setDataRisoluzione(LocalDateTime.now());

        issueRepository.save(issue);
    }

    public void rilasciaIssue(int issueId) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new RuntimeException("Issue non trovata"));

        issue.setStato(StatoIssue.TO_DO);
        issue.setAssignee(null);

        issueRepository.save(issue);
    }

    public List<Issue> getIssueAttive() {
        return issueRepository.findByStatoIn(List.of(StatoIssue.TO_DO, StatoIssue.ASSEGNATO));
    }

    public List<Issue> getIssueArchiviate() {
        return issueRepository.findByStatoIn(List.of(StatoIssue.RISOLTO, StatoIssue.ARCHIVIATO));
    }

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
