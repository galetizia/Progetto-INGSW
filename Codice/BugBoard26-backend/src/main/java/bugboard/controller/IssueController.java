package bugboard.controller;

import bugboard.enums.TipoIssue;
import bugboard.model.Issue;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import bugboard.service.IssueService;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.logging.FileHandler;

@RestController
@RequestMapping("/api/user")
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
}
