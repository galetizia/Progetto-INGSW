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
import javafx.stage.Stage;

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

    IssueClient issueClient = new IssueClient();


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
