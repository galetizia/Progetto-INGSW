package org.example.bugboard26frontend.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.bugboard26frontend.helper.IssueActionHandler;
import org.example.bugboard26frontend.helper.MyAlert;
import org.example.bugboard26frontend.helper.Validator;

import java.io.File;


/**
 * Controller dedicato alla finestra per la creazione di una nuova Issue.
 * Gestisce l'acquisizione dei dati testuali, la selezione delle enumerazioni
 * tramite menu a tendina e il caricamento opzionale di file allegati.
 */
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

    /**
     * Cattura la selezione della priorità "ALTA" nel menu a tendina,
     * aggiornando la variabile di stato interna e l'etichetta visibile all'utente.
     */
    @FXML
    protected void onPrioritaAltaSelezionata(){
        prioritaScelta = "ALTA";
        prioritaMenuButton.setText("Priorità: Alta");
    }

    /**
     * Cattura la selezione della priorità "MEDIA" nel menu a tendina,
     * aggiornando la variabile di stato interna e l'etichetta visibile all'utente.
     */
    @FXML
    protected void onPrioritaMediaSelezionata(){
        prioritaScelta = "MEDIA";
        prioritaMenuButton.setText("Priorità: Media");
    }

    /**
     * Cattura la selezione della priorità "BASSA" nel menu a tendina,
     * aggiornando la variabile di stato interna e l'etichetta visibile all'utente.
     */
    @FXML
    protected void onPrioritaBassaSelezionata(){
        prioritaScelta = "BASSA";
        prioritaMenuButton.setText("Priorità: Bassa");
    }

    /**
     * Cattura la selezione della tipologia "BUG" nel menu a tendina,
     * aggiornando la variabile di stato interna e l'etichetta visibile all'utente.
     */
    @FXML
    protected void onBugSelezionato(){
        tipologiaScelta = "BUG";
        tipologiaMenuButton.setText("Tipologia: Bug");
    }

    /**
     * Cattura la selezione della tipologia "FEATURE" nel menu a tendina,
     * aggiornando la variabile di stato interna e l'etichetta visibile all'utente.
     */
    @FXML
    protected void onFeatureSelezionato(){
        tipologiaScelta = "FEATURE";
        tipologiaMenuButton.setText("Tipologia: Feature");
    }

    /**
     * Cattura la selezione della tipologia "DOCUMENTATION" nel menu a tendina,
     * aggiornando la variabile di stato interna e l'etichetta visibile all'utente.
     */
    @FXML
    protected void onDocumentationSelezionato(){
        tipologiaScelta = "DOCUMENTATION";
        tipologiaMenuButton.setText("Tipologia: Documentation");
    }

    /**
     * Cattura la selezione della tipologia "QUESTION" nel menu a tendina,
     * aggiornando la variabile di stato interna e l'etichetta visibile all'utente.
     */
    @FXML
    protected void onQuestionSelezionato(){
        tipologiaScelta = "QUESTION";
        tipologiaMenuButton.setText("Tipologia: Question");
    }

    /**
     * Apre una finestra di dialogo di sistema per permettere all'utente
     * di selezionare un'immagine da allegare alla segnalazione.
     * Applica automaticamente un filtro per mostrare solo i formati grafici supportati (.png, .jpg, .jpeg).
     */
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

    /**
     * Gestisce il flusso di convalida e invio del modulo di segnalazione.
     * Verifica la validità della sessione e la presenza dei campi obbligatori (Titolo, Descrizione, Tipologia).
     * Se i controlli hanno esito positivo, delega la richiesta HTTP all'helper dedicato e, in caso di successo, chiude il pop-up.
     */
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


    /**
     * Annulla l'operazione in corso e chiude immediatamente la finestra di dialogo
     * scartando eventuali dati inseriti nel form.
     */
    @FXML
    protected void onIndietroButtonClick() {
        Stage stage = (Stage) indietroButton.getScene().getWindow();
        stage.close();
    }
}
