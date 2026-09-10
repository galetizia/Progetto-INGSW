package bugboard.controller;

import bugboard.enums.TipoIssue;
import bugboard.model.Issue;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import bugboard.service.IssueService;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.security.Principal;
import java.util.List;
import java.util.logging.FileHandler;

@RestController
@RequestMapping("/api/issues")
public class IssueController {

    private final IssueService issueService;

    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    @PostMapping("/nuovaIssue")
    public ResponseEntity<String> nuovaIssue(@RequestParam("titolo") String titolo,
                                             @RequestParam("descrizione") String descrizione,
                                             @RequestParam("tipologia") TipoIssue tipologia,
                                             @RequestParam(value="priorita", required = false) String priorita,
                                             @RequestParam(value="file", required = false) MultipartFile file) {
        try{
            issueService.createIssue(titolo, descrizione, tipologia, priorita, file);
            return ResponseEntity.ok("Nuova issue creata ");
        } catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/elenco_issue")
    public ResponseEntity<List<Issue>> elencoIssue() {
            return ResponseEntity.ok(issueService.elencoIssue());
    }

    @PostMapping("/prendiInCarico")
    public ResponseEntity<String> prendiInCarico(@RequestParam int id, Principal principal){

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

    @GetMapping("/attive")
    public ResponseEntity<List<Issue>> getIssueAttive() {
        return ResponseEntity.ok(issueService.getIssueAttive());
    }

    @GetMapping("/storico")
    public ResponseEntity<List<Issue>> getIssueArchiviate() {
        return ResponseEntity.ok(issueService.getIssueArchiviate());
    }

    // Endpoint accessibile SOLO all'amministratore
    @PutMapping("/{id}/archivia")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> archiviaIssue(@PathVariable int id) {
        try {
            Issue issueArchiviata = issueService.archiviaIssue(id);
            return ResponseEntity.ok(issueArchiviata);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
