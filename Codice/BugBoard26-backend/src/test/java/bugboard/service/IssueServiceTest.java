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

/**
 * Classe di test per {@link IssueService}.
 * Utilizza JUnit 5 e Mockito per testare la logica di business isolandola
 * dalle dipendenze esterne. Si concentra sulla verifica
 * del comportamento del servizio durante le operazioni sulle {@link Issue},
 * {@code @ExtendWith(MockitoExtension.class) serve per abilitare integrazione tra JUnit e Mockito}
 */

@ExtendWith(MockitoExtension.class)
class IssueServiceTest {

    /**
     * Mock del repository delle issue.
     * Permette di simulare le operazioni sul database
     * definendone il comportamento all'interno dei test.
     */
    @Mock
    private IssueRepository issueRepository;

    /**
     * Mock del repository degli utenti.
     */
    @Mock
    private AuthUserRepository authUserRepository;

    /**
     * L'istanza del servizio da testare.
     * L'annotazione {@code @InjectMocks} si occupa di creare l'oggetto e
     * iniettare automaticamente i mock definiti sopra al suo interno.
     */
    @InjectMocks
    private IssueService issueService;

    /**
     * Testa il caso di successo nell'assegnazione di una issue a un utente.
     * Verifica che, fornendo un ID e un'email esistenti:
     *      L'operazione ritorni "true".
     *      Lo stato della issue venga aggiornato ad "ASSEGNATO".
     *      L'utente venga correttamente impostato come "assignee".
     *      La data di assegnazione non sia nulla.
     *      Il metodo di salvataggio del repository venga invocato esattamente una volta.
     */
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

        when(issueRepository.findById(idIssue)).thenReturn(Optional.of(issue));
        when(authUserRepository.findByEmail(email)).thenReturn(Optional.of(user));

        boolean result = issueService.assegnaIssueUtente(idIssue, email);

        assertTrue(result, "Il metodo dovrebbe restituire true in caso di successo");
        assertEquals(StatoIssue.ASSEGNATO, issue.getStato(), "Lo stato della issue deve passare ad ASSEGNATO");
        assertEquals(user, issue.getAssignee(), "L'utente assegnato deve coincidere con quello trovato");
        assertNotNull(issue.getDataAssegnazione(), "La data di assegnazione non deve essere nulla");

        verify(issueRepository, times(1)).save(issue);
    }

    /**
     * Testa il caso di fail quando la issue non viene trovata nel DB.
     * Verifica che:
     *      Il metodo restituisca "false".
     *      La ricerca dell'utente non venga mai effettuata.
     *      Il salvataggio della issue non venga mai invocato.
     */
    @Test
    @DisplayName("FAIL: Issue non trovata")
    void testAssegnaIssueUtente_IssueNonTrovata() {
        int idIssue = 2;
        String email = "testAssegnazione2@bugboard.com";

        when(issueRepository.findById(idIssue)).thenReturn(Optional.empty());

        boolean result = issueService.assegnaIssueUtente(idIssue, email);

        assertFalse(result, "Il metodo dovrebbe restituire false se l'issue non esiste");

        verify(authUserRepository, never()).findByEmail(anyString());

        verify(issueRepository, never()).save(any(Issue.class));
    }

    /**
     * Testa il caso di fallimento quando la issue viene trovata, ma l'utente non esiste.
     * Verifica che:
     *      Il metodo restituisca "false".
     *      Modifica e salvataggio della issue non vengano mai effettuati.
     */
    @Test
    @DisplayName("FAIL: Utente non trovato")
    void testAssegnaIssueUtente_UtenteNonTrovato() {
        int issueId = 3;
        String email = "utentenontrovato@bugboard.com";

        Issue issue = new Issue();
        issue.setId(issueId);

        when(issueRepository.findById(issueId)).thenReturn(Optional.of(issue));
        when(authUserRepository.findByEmail(email)).thenReturn(Optional.empty());

        boolean result = issueService.assegnaIssueUtente(issueId, email);

        assertFalse(result, "Il metodo dovrebbe restituire false se l'utente non viene trovato");

        verify(issueRepository, never()).save(any(Issue.class));
    }


}