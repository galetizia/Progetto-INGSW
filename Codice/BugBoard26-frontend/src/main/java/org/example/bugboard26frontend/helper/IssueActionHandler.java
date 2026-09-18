package org.example.bugboard26frontend.helper;

import client.AuthSession;
import client.IssueClient;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import model.Issue;

import java.io.File;

public class IssueActionHandler {

    private static final IssueClient issueClient = new IssueClient();

    private IssueActionHandler() {}


    public static void archivia(Issue issue, Runnable onSuccess){

        if (issue != null && AuthSession.getInstance().isLoggedIn()) {
            Alert conferma = new Alert(Alert.AlertType.CONFIRMATION);
            conferma.setTitle("Conferma Archiviazione");
            conferma.setHeaderText("Archiviazione Issue #" + issue.getId());
            conferma.setContentText("Sei sicuro di voler archiviare: '" + issue.getTitolo() + "'?");

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


    public static void rilascia(Issue issue, Runnable onSuccess){
        if (issue == null || !AuthSession.getInstance().isLoggedIn()) {
            return;
        }
        Alert conferma = new Alert(Alert.AlertType.CONFIRMATION);
        conferma.setTitle("Conferma");
        conferma.setHeaderText("Rilascio Issue #" + issue.getId());
        conferma.setContentText("Sei sicuro di voler rimettere questa issue in stato TO_DO?");

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


    public static void creazioneIssue(String titolo, String descrizione, String priorita, String tipologia, File file, Runnable onSuccess){
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Conferma operazione");
        confirmationAlert.setHeaderText("Stai per creare una nuova issue");
        confirmationAlert.setContentText("Procedere?");
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
