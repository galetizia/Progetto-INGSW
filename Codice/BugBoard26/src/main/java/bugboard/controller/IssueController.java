package bugboard.controller;

import bugboard.model.Issue;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import bugboard.service.IssueService;

@RestController
@RequestMapping("/api/issue")
public class IssueController {

    private final IssueService issueService;

    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    @PostMapping("/nuovaIssue")
    public ResponseEntity<String> nuovaIssue(@RequestBody IssueRequest request) {
        try{
            issueService.createIssue(request.titolo(),request.descrizione(),request.priorita(),request.urlImmagine());
            return ResponseEntity.ok("Nuova issue creata ");
        } catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

record IssueRequest(String titolo, String descrizione, String priorita, String urlImmagine) {}