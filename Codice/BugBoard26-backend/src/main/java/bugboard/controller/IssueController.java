package bugboard.controller;

import bugboard.enums.TipoIssue;
import bugboard.model.Issue;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import bugboard.service.IssueService;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Map;

/**
 * Gestisce le richieste HTTP per l'assegnazione, la risoluzione e l'archiviazione delle Issue.
 */
@RestController
@RequestMapping("/api/issues")
public class IssueController {
    private final IssueService issueService;


    /**
     * Inizializza il controller passando il service per le operazioni sulle issue.
     *
     * @param issueService Il service che contiene la logica di business per le issue.
     */
    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    /**
     * Crea e salva nel database una nuova Issue, gestendo anche l'eventuale caricamento di un allegato.
     *
     * @param titolo      Il titolo del problema.
     * @param descrizione I dettagli della segnalazione.
     * @param tipologia   La classificazione della issue (es. BUG, FEATURE...).
     * @param priorita    Il livello di urgenza della segnalazione (opzionale).
     * @param file        L'eventuale file multimediale allegato (opzionale).
     * @return Un messaggio di conferma della creazione, o un errore 400 in caso di dati non validi o problemi di I/O.
     */
    @PostMapping("/nuova-issue")
    public ResponseEntity<String> nuovaIssue(@RequestParam("titolo") String titolo,
                                             @RequestParam("descrizione") String descrizione,
                                             @RequestParam("tipologia") TipoIssue tipologia,
                                             @RequestParam(value="priorita", required = false) String priorita,
                                             @RequestParam(value="file", required = false) MultipartFile file) {
        try{
            issueService.createIssue(titolo, descrizione, tipologia, priorita, file);
            return ResponseEntity.ok("Nuova issue creata");
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    /**
     * Recupera l'elenco completo di tutte le issue presenti nel database.
     *
     * @return Una lista contenente tutte le issue.
     */
    @GetMapping("/elenco-issue")
    public ResponseEntity<List<Issue>> elencoIssue() {
            return ResponseEntity.ok(issueService.elencoIssue());
    }


    /**
     * Fornisce il conteggio delle issue raggruppate per il loro stato.
     *
     * @return Una mappa che associa ogni stato al relativo numero di issue.
     */
    @GetMapping("/count-issue-states")
    public ResponseEntity<Map<String, Integer>> countIssueStates() {
        return ResponseEntity.ok(issueService.countIssueStates());
    }


    /**
     * Fornisce il conteggio delle issue raggruppate per tipologia.
     *
     * @return Una mappa che associa ogni tipologia al relativo numero di issue.
     */
    @GetMapping("/count-issue-types")
    public ResponseEntity<Map<String, Integer>> countIssueTypes() {
        return ResponseEntity.ok(issueService.countIssueTypes());
    }


    /**
     * Permette all'utente autenticato di prendere in carico autonomamente
     * una issue specifica, cambiando lo stato dell'issue in "ASSEGNATO".
     *
     * @param id        L'identificativo della issue da prendere in carico.
     * @param principal L'oggetto passato da Spring Security che indica l'utente attualmente loggato.
     * @return Messaggio di conferma o errore nel caso l'issue non sia trovabile.
     */
    @PostMapping("/{id}/prendi-in-carico")
    public ResponseEntity<String> prendiInCarico(@PathVariable int id, Principal principal){
        try{
            String email = principal.getName();
            boolean successo = issueService.assegnaIssueUtente(id, email);
            if(successo){
                return ResponseEntity.ok("Issue presa in carico");
            }
            else {
                return ResponseEntity.badRequest().body("Impossibile trovare o prendere in carico issue");
            }
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore del server: " + e.getMessage());
        }

    }


    /**
     * Recupera tutte le issue come "attive" (quindi nello stato TO_DO o ASSEGNATO).
     *
     * @return La lista delle issue attualmente assegnate o TO_DO.
     */
    @GetMapping("/attive")
    public ResponseEntity<List<Issue>> getIssueAttive() {
        return ResponseEntity.ok(issueService.getIssueAttive());
    }


    /**
     * Recupera tutte le issue terminate (quindi nello stato RISOLTO o ARCHIVIATO).
     *
     * @return La lista delle issue inattive.
     */
    @GetMapping("/storico")
    public ResponseEntity<List<Issue>> getIssueArchiviate() {
        return ResponseEntity.ok(issueService.getIssueArchiviate());
    }


    /**
     * Elimina definitivamente una issue dal database. Operazione critica riservata agli amministratori.
     *
     * @param id L'identificativo della issue da rimuovere.
     * @return Messaggio di conferma dell'avvenuta eliminazione o errore in caso di fallimento.
     */
    @DeleteMapping("/{id}/elimina")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteIssue(@PathVariable int id) {
        try{
            boolean success = issueService.eliminaIssue(id);
            if(success){
                return ResponseEntity.ok("Issue eliminata con successo");
            } else {
                return ResponseEntity.badRequest().body("Errore nell'eliminazione della issue");
            }

        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore del server: " + e.getMessage());
        }
    }


    /**
     * Modifica lo stato di una issue nello stato "ARCHIVIATO". Operazione riservata agli amministratori.
     *
     * @param id L'identificativo della issue da archiviare.
     * @return L'oggetto issue aggiornato o un errore se l'ID non è valido.
     */
    @PutMapping("/{id}/archivia")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> archiviaIssue(@PathVariable int id) {
        try {
            Issue issueArchiviata = issueService.archiviaIssue(id);
            return ResponseEntity.ok(issueArchiviata);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    /**
     * Contrassegna una issue come risolta, metodo chiamato dall'utente a cui è assegnata.
     *
     * @param id L'identificativo della issue da risolvere.
     * @return Messaggio di conferma o eventuale errore di qualsiasi tipo.
     */
    @PutMapping("/{id}/risolvi")
    public ResponseEntity<String> risolviIssue(@PathVariable int id) {
        try {
            issueService.risolviIssue(id);
            return ResponseEntity.ok("Issue risolta con successo");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    /**
     * Rimuove l'assegnazione da una issue e la riporta allo stato TO_DO, rendendola di nuovo disponibile a tutti gli utenti.
     *
     * @param id L'identificativo della issue da rilasciare.
     * @return Messaggio di conferma o eventuale errore di qualsiasi tipo.
     */
    @PutMapping("/{id}/rilascia")
    public ResponseEntity<String> rilasciaIssue(@PathVariable int id) {
        try {
            issueService.rilasciaIssue(id);
            return ResponseEntity.ok("Issue rilasciata con successo");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
