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
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gestisce il ciclo di vita delle Issue all'interno del sistema, dall'apertura fino all'archiviazione,
 * inclusa l'elaborazione delle statistiche.
 */
@Service
public class IssueService {
    private final IssueRepository issueRepository;
    private final AuthUserRepository authUserRepository;


    /**
     * Inizializza il service con le repository necessarie per interagire col database.
     *
     * @param issueRepository    La repository delle issue.
     * @param authUserRepository La repository degli utenti.
     */
    public IssueService(IssueRepository issueRepository, AuthUserRepository authUserRepository) {
        this.issueRepository = issueRepository;
        this.authUserRepository = authUserRepository;
    }


    /**
     * Recupera l'elenco delle issue attive, ovvero in stato "TO_DO" o "ASSEGNATO".
     *
     * @return La lista delle issue attualmente attive.
     */
    public List<Issue> getIssueAttive() {
        return issueRepository.findByStatoIn(List.of(StatoIssue.TO_DO, StatoIssue.ASSEGNATO));
    }


    /**
     * Recupera l'elenco delle issue terminate, ovvero in stato "RISOLTO" o "ARCHIVIATO".
     *
     * @return La lista delle issue terminate.
     */
    public List<Issue> getIssueArchiviate() {
        return issueRepository.findByStatoIn(List.of(StatoIssue.RISOLTO, StatoIssue.ARCHIVIATO));
    }


    /**
     * Recupera l'elenco completo di tutte le issue presenti nel database.
     *
     * @return La lista delle issue.
     */
    public List<Issue> elencoIssue(){
        return issueRepository.findAll();
    }


    /**
     * Conta e raggruppa le issue in base al loro stato attuale.
     *
     * @return Una mappa che associa il nome dello stato al totale delle issue in quello stato.
     */
    public Map<String, Integer> countIssueStates() {
        List<Object[]> issueStates = issueRepository.findAllIssueStates();
        return insertInMapIssue(issueStates);
    }


    /**
     * Conta e raggruppa le issue in base alla loro tipologia.
     *
     * @return Una mappa che associa il nome della tipologia al relativo totale di issue.
     */
    public Map<String, Integer> countIssueTypes() {
        List<Object[]> issueTypes = issueRepository.findAllIssueTypes();
        return insertInMapIssue(issueTypes);
    }


    /**
     * Metodo di supporto per trasformare le liste di risultati aggregati provenienti dal database in mappe chiave-valore.
     *
     * @param list I dati estratti dalle query.
     * @return La mappa elaborata e tipizzata.
     */
    private Map<String, Integer> insertInMapIssue(List<Object[]> list){
        Map<String, Integer> map = new HashMap<>();
        for (Object[] obj : list) {
            String tipo = String.valueOf(obj[0]);
            Integer count = ((Number) obj[1]).intValue();
            map.put(tipo, count);
        }
        return map;
    }


    /**
     * Segna una issue come completata aggiornando il suo stato e impostando la data di risoluzione esatta.
     *
     * @param issueId L'identificativo della issue da risolvere.
     * @throws RuntimeException Se la issue non è presente nel database.
     */
    public void risolviIssue(int issueId) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new RuntimeException("Issue non trovata"));

        issue.setStato(StatoIssue.RISOLTO);
        issue.setDataRisoluzione(LocalDateTime.now(ZoneId.systemDefault()));

        issueRepository.save(issue);
    }


    /**
     * Rimuove l'assegnatario da una issue in corso, annulla la data di assegnazione e la riporta tra quelle da prendere in carico.
     *
     * @param issueId L'identificativo della issue da rilasciare.
     * @throws RuntimeException Se la issue non è presente nel database.
     */
    public void rilasciaIssue(int issueId) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new RuntimeException("Issue non trovata"));

        issue.setStato(StatoIssue.TO_DO);
        issue.setAssignee(null);
        issue.setDataAssegnazione(null);

        issueRepository.save(issue);
    }


    /**
     * Elimina definitivamente una issue dal database.
     *
     * @param idIssue L'ID della issue da eliminare.
     * @return true se l'eliminazione va a buon fine, false se la issue non esisteva.
     */
    public boolean eliminaIssue(int idIssue) {
        if(issueRepository.existsById(idIssue)) {
            issueRepository.deleteById(idIssue);
            return true;
        }
        return false;
    }


    /**
     * Archivia in modo permanente una issue chiusa, rimuovendola da quelle disponibili o assegnate.
     *
     * @param idIssue L'ID della issue da archiviare.
     * @return L'oggetto issue aggiornato.
     * @throws IllegalArgumentException Se non viene trovata alcuna issue con l'id specificato.
     */
    public Issue archiviaIssue(int idIssue) {
        Issue issue = issueRepository.findById(idIssue)
                .orElseThrow(() -> new IllegalArgumentException("Issue non trovata con ID: " + idIssue));

        issue.setStato(StatoIssue.ARCHIVIATO);

        return issueRepository.save(issue);
    }


    /**
     * Assegna una issue a un utente, registrando il momento esatto dell'assegnazione e aggiornandone lo stato.
     *
     * @param issueId   L'ID della issue da assegnare.
     * @param emailUser L'email dell'utente che se ne fa carico.
     * @return true se l'operazione ha successo, false se si verifica un errore durante il recupero dei dati.
     */
    public boolean assegnaIssueUtente(int issueId, String emailUser) {
        try{
            Issue issue = issueRepository.findById(issueId)
                    .orElseThrow(() -> new RuntimeException("Issue non trovata"));

            AuthUser user = authUserRepository.findByEmail(emailUser).orElseThrow(() ->
                    new RuntimeException("Utente non trovato"));

            issue.setStato(StatoIssue.ASSEGNATO);
            issue.setAssignee(user);
            issue.setDataAssegnazione(LocalDateTime.now(ZoneId.systemDefault()));

            issueRepository.save(issue);
            return true;
        } catch(Exception _){
            return false;
        }

    }


    /**
     * Crea e memorizza una nuova issue, gestendo anche il salvataggio di un eventuale file allegato.
     *
     * @param titolo      Il titolo del problema.
     * @param descrizione I dettagli del problema.
     * @param tipologia   La tipologia della issue (es. Bug, Feature).
     * @param priorita    L'urgenza (opzionale).
     * @param file        Un file caricato per supportare la segnalazione (opzionale).
     * @throws RuntimeException Se si verifica un errore I/O durante la lettura di byte del file allegato.
     */
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
}
