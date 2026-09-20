package bugboard.service;

import bugboard.enums.StatoIssue;
import bugboard.model.AuthUser;
import bugboard.model.Issue;
import bugboard.repository.AuthUserRepository;
import bugboard.repository.IssueRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Abilita integrazione JUnit e Mockito
@ExtendWith(MockitoExtension.class)
class IssueServiceTest {

    //@Mock crea un componente finto. Non accede al DB ma ci permette di deciderne il comportamento
    @Mock
    private IssueRepository issueRepository;
    @Mock
    private AuthUserRepository authUserRepository;

    //prende i mock e li inietta
    @InjectMocks
    private IssueService issueService;

    @Test
    @DisplayName("Issue assegnata con successo")
    void testAssegnaIssueUtente_Success(){

        int idIssue = 1;
        String email = "testAssegnazione@bugboard.com";

        Issue issue = new Issue();
        issue.setId(idIssue);
        issue.setStato(StatoIssue.TO_DO);

        AuthUser user = new AuthUser();
        user.setEmail(email);

        // Istruiamo i mock su come rispondere
        when(issueRepository.findById(idIssue)).thenReturn(Optional.of(issue));
        when(authUserRepository.findByEmail(email)).thenReturn(Optional.of(user));

        boolean result = issueService.assegnaIssueUtente(idIssue, email);

        assertTrue(result, "Il metodo dovrebbe restituire true in caso di successo");
        assertEquals(StatoIssue.ASSEGNATO, issue.getStato(), "Lo stato della issue deve passare ad ASSEGNATO");
        assertEquals(user, issue.getAssignee(), "L'utente assegnato deve coincidere con quello trovato");
        assertNotNull(issue.getDataAssegnazione(), "La data di assegnazione non deve essere nulla");

        // Verifica che il salvataggio sul DB sia stato invocato esattamente 1 volta
        verify(issueRepository, times(1)).save(issue);

    }

    @Test
    @DisplayName("FAIL: Issue non trovata")
    void testAssegnaIssueUtente_IssueNonTrovata() {
        int idIssue = 2;
        String email = "testAssegnazione2@bugboard.com";

        // Simuliamo che il DB non trovi nessuna issue
        when(issueRepository.findById(idIssue)).thenReturn(Optional.empty());

        boolean result = issueService.assegnaIssueUtente(idIssue, email);

        assertFalse(result, "Il metodo dovrebbe restituire false se l'issue non esiste");

        // Verifichiamo che la ricerca dell'utente NON venga mai eseguita
        verify(authUserRepository, never()).findByEmail(anyString());

        // Verifichiamo che non venga mai tentato il salvataggio
        verify(issueRepository, never()).save(any(Issue.class));
    }

    @Test
    @DisplayName("FAIL: Utente non trovato")
    void testAssegnaIssueUtente_UtenteNonTrovato() {
        int issueId = 3;
        String email = "utentenontrovato@bugboard.com";

        Issue issue = new Issue();
        issue.setId(issueId);

        // Simuliamo che l'issue venga trovata, ma l'utente no
        when(issueRepository.findById(issueId)).thenReturn(Optional.of(issue));
        when(authUserRepository.findByEmail(email)).thenReturn(Optional.empty());

        boolean result = issueService.assegnaIssueUtente(issueId, email);

        assertFalse(result, "Il metodo dovrebbe restituire false se l'utente non viene trovato");

        // Verifichiamo che, nonostante l'issue esista, non venga mai salvata
        verify(issueRepository, never()).save(any(Issue.class));
    }


}