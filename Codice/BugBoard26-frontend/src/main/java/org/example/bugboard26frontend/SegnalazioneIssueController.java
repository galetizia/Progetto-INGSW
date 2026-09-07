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
    private TextField descrizioneField;

    @FXML
    private TextField prioritaField;

    @FXML
    private MenuButton prioritaMenuButton;
    private String prioritaScelta= "";

    @FXML
    private MenuButton tipologiaMenuButton;
    private String tipologiaScelta = "";

    IssueClient issueClient = new IssueClient();

    @FXML
    protected void onPrioritaAltaSelezionata(){
        prioritaScelta = "Alta";
        prioritaMenuButton.setText("Priorità: Alta");
    }

    @FXML
    protected void onPrioritaMediaSelezionata(){
        prioritaScelta = "Media";
        prioritaMenuButton.setText("Priorità: Media");
    }

    @FXML
    protected void onPrioritaBassaSelezionata(){
        prioritaScelta = "Bassa";
        prioritaMenuButton.setText("Priorità: Bassa");
    }

    @FXML
    protected void onBugSelezionato(){
        tipologiaScelta = "Bug";
        tipologiaMenuButton.setText("Tipologia: Bug");
    }

    @FXML
    protected void onFeatureSelezionato(){
        tipologiaScelta = "Feature";
        tipologiaMenuButton.setText("Tipologia: Feature");
    }

    @FXML
    protected void onDocumentationSelezionato(){
        tipologiaScelta = "Documentation";
        tipologiaMenuButton.setText("Tipologia: Documentation");
    }

    @FXML
    protected void onQuestionSelezionato(){
        tipologiaScelta = "Question";
        tipologiaMenuButton.setText("Tipologia: Question");
    }

    @FXML
    protected void onAllegaButtonClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleziona un'immagine da allegare");

        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Immagini", "*.png", "*.jpg", "*.jpeg"));

        File file = fileChooser.showOpenDialog(null);
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
        String priorita = prioritaField.getText();

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Conferma operazione");
            alert.setHeaderText("Stai per creare una nuova issue");
            alert.setContentText("Procedere?");
            alert.showAndWait().ifPresent(response -> {
                if(response == ButtonType.OK){
                    System.out.println("Salvataggio in corso");
                    boolean success = issueClient.createIssue(titolo, descrizione, priorita, null);
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
