package org.example.bugboard26frontend;

import client.AuthClient;
import client.IssueClient;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import client.AuthSession;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class SegnalazioneIssueController {

    @FXML
    private TextField titoloField;

    @FXML
    private Button confermaButton;

    @FXML
    private TextArea descrizioneField;

    @FXML
    private MenuButton prioritaMenuButton;
    private String prioritaScelta= "";

    @FXML
    private MenuButton tipologiaMenuButton;
    private String tipologiaScelta = "";

    IssueClient issueClient = new IssueClient();

    File file = null;

    @FXML
    protected void onPrioritaAltaSelezionata(){
        prioritaScelta = "ALTA";
        prioritaMenuButton.setText("Priorità: Alta");
    }

    @FXML
    protected void onPrioritaMediaSelezionata(){
        prioritaScelta = "MEDIA";
        prioritaMenuButton.setText("Priorità: Media");
    }

    @FXML
    protected void onPrioritaBassaSelezionata(){
        prioritaScelta = "BASSA";
        prioritaMenuButton.setText("Priorità: Bassa");
    }

    @FXML
    protected void onBugSelezionato(){
        tipologiaScelta = "BUG";
        tipologiaMenuButton.setText("Tipologia: Bug");
    }

    @FXML
    protected void onFeatureSelezionato(){
        tipologiaScelta = "FEATURE";
        tipologiaMenuButton.setText("Tipologia: Feature");
    }

    @FXML
    protected void onDocumentationSelezionato(){
        tipologiaScelta = "DOCUMENTATION";
        tipologiaMenuButton.setText("Tipologia: Documentation");
    }

    @FXML
    protected void onQuestionSelezionato(){
        tipologiaScelta = "QUESTION";
        tipologiaMenuButton.setText("Tipologia: Question");
    }

    @FXML
    protected void onAllegaButtonClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleziona un'immagine da allegare");

        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Immagini", "*.png", "*.jpg", "*.jpeg"));

        file = fileChooser.showOpenDialog(null);
        if (file != null) {
            System.out.println("File scelto: " + file.getAbsolutePath());
        } else {
            System.out.println("Nessun file selezionato");
        }
    }

    @FXML
    protected void onConfermaButtonClick(){
        String titolo = titoloField.getText();
        String descrizione = descrizioneField.getText();

        if ( titolo.isBlank() || tipologiaScelta.isBlank() || descrizione.isBlank()) {
            Alert alertErrore = new Alert(Alert.AlertType.WARNING);
            alertErrore.setTitle("Dati mancanti");
            alertErrore.setHeaderText(null);
            alertErrore.setContentText("Inserire Titolo, Descrizione e Tipologia.");
            alertErrore.showAndWait();
            return;
        }

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Conferma operazione");
            alert.setHeaderText("Stai per creare una nuova issue");
            alert.setContentText("Procedere?");
            alert.showAndWait().ifPresent(response -> {
                if(response == ButtonType.OK){
                    System.out.println("Salvataggio in corso");
                    boolean success = issueClient.createIssue(titolo, descrizione, prioritaScelta, tipologiaScelta, file);
                    if(success){
                        Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
                        alert2.setTitle("Issue creata");
                        alert2.setHeaderText(null);
                        alert2.setContentText("Issue creata con successo");
                        alert2.showAndWait();
                    } else {
                        Alert alert2 = new Alert(Alert.AlertType.ERROR);
                        alert2.setTitle("Errore!");
                        alert2.setHeaderText(null);
                        alert2.setContentText("Errore nella creazione dell'issue!");
                        alert2.showAndWait();
                    }
                } else {
                    System.out.println("Operazione annullata");
                }
            });



    }
}
