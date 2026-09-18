package org.example.bugboard26frontend.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.bugboard26frontend.helper.IssueActionHandler;
import org.example.bugboard26frontend.helper.MyAlert;
import org.example.bugboard26frontend.helper.Validator;

import java.io.File;


public class SegnalazioneIssueController {

    @FXML
    private TextField titoloField;
    @FXML
    private Button confermaButton;

    @FXML
    private Button indietroButton;

    @FXML
    private TextArea descrizioneField;

    @FXML
    private MenuButton prioritaMenuButton;
    private String prioritaScelta= "";

    @FXML
    private MenuButton tipologiaMenuButton;
    private String tipologiaScelta = "";

    private File file = null;

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

        Stage stage = (Stage) titoloField.getScene().getWindow();
        file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            MyAlert.mostraAlert(Alert.AlertType.INFORMATION, "Successo", "Immagine allegata con successo");
        }
    }

    @FXML
    protected void onConfermaButtonClick(){
        Stage stage = (Stage) confermaButton.getScene().getWindow();

        if(Validator.sessionInvalid(stage)) return;

        String titolo = titoloField.getText();
        String descrizione = descrizioneField.getText();

        if (titolo.isBlank() || tipologiaScelta.isBlank() || descrizione.isBlank()) {
            MyAlert.mostraAlert(Alert.AlertType.WARNING, "Dati mancanti", "Inserire Titolo, Descrizione e Tipologia.");
            return;
        }

        Validator.backEndValidator(() ->
                IssueActionHandler.creazioneIssue(titolo, descrizione, prioritaScelta, tipologiaScelta, file, stage::close));
    }


    @FXML
    protected void onIndietroButtonClick() {
        Stage stage = (Stage) indietroButton.getScene().getWindow();
        stage.close();
    }
}
