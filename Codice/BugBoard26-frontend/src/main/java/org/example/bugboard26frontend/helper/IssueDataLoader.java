package org.example.bugboard26frontend.helper;

import client.IssueClient;
import enums.Ruolo;
import javafx.collections.ObservableList;
import model.Issue;

import java.util.List;

/**
 * Classe Helper incaricata di recuperare le Issue dal server
 * e popolare le strutture dati reattive collegate all'interfaccia grafica JavaFX.
 */
public class IssueDataLoader {
    private static final IssueClient issueClient = new IssueClient();

    private IssueDataLoader() {}

    /**
     * Esegue la chiamata di rete per scaricare le issue e aggiorna la lista osservabile della tabella.
     * La logica di recupero si adatta dinamicamente: differenzia tra la vista "Storico" e "Attive",
     * e applica regole di visibilità specifiche in base al ruolo dell'utente.
     *
     * @param ruolo      Il ruolo dell'utente loggato, determinante per i permessi di visualizzazione.
     * @param masterData La lista osservabile collegata alla TableView grafica da aggiornare con i nuovi dati.
     * @param isArchivio Se true, carica le issue risolte o archiviate; se false, carica quelle attive.
     */
    public static void loadOnTable(Ruolo ruolo, ObservableList<Issue> masterData, boolean isArchivio) {
        if (isArchivio) {
            List<Issue> issues = issueClient.getIssueArchiviate();
            masterData.setAll(issues);
        }
        else {
            if(ruolo == Ruolo.INTERNAL_USER ||  ruolo == Ruolo.ADMIN) {
                List<Issue> issues = issueClient.getIssueAttive();
                masterData.setAll(issues);
            } else if(ruolo == Ruolo.EXTERNAL_USER) {
                List<Issue> issues = issueClient.elencoIssue();
                issues.removeIf(issue -> "ARCHIVIATO".equalsIgnoreCase(issue.getStato()));
                masterData.setAll(issues);
            }
        }
    }
}

