package org.example.bugboard26frontend.helper;

import client.AuthSession;
import client.IssueClient;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import model.Issue;

import java.io.File;

/**
 * Classe Helper che funge da intermediario tra le interazioni dell'utente sull'interfaccia JavaFX
 * e le chiamate di rete verso il backend.
 * Gestisce le finestre di conferma, l'esecuzione delle operazioni e le notifiche visive di successo o errore.
 */
public class IssueActionHandler {

    private static final IssueClient issueClient = new IssueClient();

    private IssueActionHandler() {}


    /**
     * Mostra un pop-up di conferma e, in caso di accettazione, invia la richiesta al server per archiviare la issue.
     *
     * @param issue     La issue selezionata dall'utente da archiviare.
     * @param onSuccess L'azione di callback da eseguire in caso di successo (es. aggiornare i dati della tabella a schermo).
     */
    public static void archivia(Issue issue, Runnable onSuccess){

        if (issue != null && AuthSession.getInstance().isLoggedIn()) {

            Alert conferma = MyAlert.mostraAlertConfirmation("Conferma Archiviazione", "Archiviazione Issue #" + issue.getId(),
                    "Sei sicuro di voler archiviare: '" + issue.getTitolo() + "'?");

            conferma.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    boolean successo = issueClient.archiviaIssue(issue.getId());

                    if (successo) {
                        MyAlert.mostraAlert(Alert.AlertType.INFORMATION,"Successo", "Issue archiviata con successo!");

                        if (onSuccess != null) onSuccess.run();

                    } else {
                        MyAlert.mostraAlert(Alert.AlertType.ERROR, "Errore", "Si è verificato un problema di comunicazione col server.");
                    }
                }
            });
        }
    }


    /**
     * Gestisce l'avanzamento di stato operativo di una issue.
     * Se la issue è nello stato TO_DO, la assegna all'utente corrente.
     * Se è già in lavorazione, la contrassegna come RISOLTA.
     *
     * @param issue     La issue da prendere in carico o da marcare come risolta.
     * @param onSuccess L'azione di callback da eseguire per aggiornare l'interfaccia grafica dopo l'operazione.
     */
    public static void prendiInCarico(Issue issue, Runnable onSuccess){
        if (issue == null || !AuthSession.getInstance().isLoggedIn()) {
            return;
        }
        boolean success = false;

        if ("TO_DO".equalsIgnoreCase(issue.getStato())) {
            success = issueClient.prendiInCarico(issue.getId());
            if (success) {
                MyAlert.mostraAlert(Alert.AlertType.INFORMATION, "Successo", "Hai preso in carico la issue #" + issue.getId());
            } else {
                MyAlert.mostraAlert(Alert.AlertType.ERROR, "Errore", "Impossibile prendere in carico la issue.");
            }
        }
        else if ("ASSEGNATO".equalsIgnoreCase(issue.getStato())) {
            success = issueClient.risolviIssue(issue.getId());
            if (success) {
                MyAlert.mostraAlert(Alert.AlertType.INFORMATION, "Successo", "Issue #" + issue.getId() + " segnata come Risolta!");
            } else {
                MyAlert.mostraAlert(Alert.AlertType.ERROR, "Errore", "Impossibile risolvere la issue.");
            }
        }
        if(success && onSuccess != null) onSuccess.run();
    }


    /**
     * Mostra una finestra di conferma e, se accettata, rimuove l'assegnazione dalla issue riportandola
     * allo stato "TO_DO", rendendola nuovamente disponibile per altri utenti.
     *
     * @param issue     La issue di cui rilasciare la lavorazione.
     * @param onSuccess L'azione di callback per ricaricare i dati visivi.
     */
    public static void rilascia(Issue issue, Runnable onSuccess){
        if (issue == null || !AuthSession.getInstance().isLoggedIn()) {
            return;
        }
        Alert conferma = MyAlert.mostraAlertConfirmation("Conferma", "Rilascio Issue #" + issue.getId(),
                "Sei sicuro di voler rimettere questa issue in stato TO_DO?");

        conferma.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean success = issueClient.rilasciaIssue(issue.getId());
                if (success) {
                    MyAlert.mostraAlert(Alert.AlertType.INFORMATION, "Successo", "Hai rilasciato la issue.");
                    if(onSuccess != null) onSuccess.run();
                } else {
                    MyAlert.mostraAlert(Alert.AlertType.ERROR, "Errore", "Si è verificato un problema di comunicazione col server.");
                }
            }
        });
    }


    /**
     * Richiede una conferma visiva all'utente prima di inviare al server i dati del modulo per la creazione
     * di una nuova issue, includendo l'eventuale file allegato.
     *
     * @param titolo      Il titolo inserito nel modulo.
     * @param descrizione I dettagli del problema.
     * @param priorita    L'urgenza selezionata.
     * @param tipologia   La classificazione tecnica.
     * @param file        Il file allegato caricato dall'utente (Opzionale).
     * @param onSuccess   L'azione di callback da eseguire in caso di successo.
     */
    public static void creazioneIssue(String titolo, String descrizione, String priorita, String tipologia, File file, Runnable onSuccess){
        Alert confirmationAlert = MyAlert.mostraAlertConfirmation("Conferma operazione", "Stai per creare una nuova issue", "Procedere?");

        confirmationAlert.showAndWait().ifPresent(response -> {

            if(response == ButtonType.OK){
                boolean success = issueClient.createIssue(titolo, descrizione, priorita, tipologia, file);
                if(success){
                    MyAlert.mostraAlert(Alert.AlertType.INFORMATION, "Issue creata", "Issue creata con successo");
                    if(onSuccess != null) onSuccess.run();
                } else {
                    MyAlert.mostraAlert(Alert.AlertType.ERROR, "Errore!", "Errore nella creazione della issue!");
                }
            }
        });
    }
}
