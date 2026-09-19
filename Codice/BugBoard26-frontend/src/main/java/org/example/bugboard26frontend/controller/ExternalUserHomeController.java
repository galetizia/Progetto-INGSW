package org.example.bugboard26frontend.controller;

import client.AuthSession;
import enums.Ruolo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import model.Issue;
import org.example.bugboard26frontend.helper.*;

/**
 * Controller della schermata dedicata all'External User.
 * Offre un'interfaccia semplificata rispetto agli utenti interni o agli amministratori,
 * limitata alla sola visualizzazione delle issue pubbliche e dei relativi allegati.
 */
public class ExternalUserHomeController {

    @FXML
    private MenuItem logoutButton;
    private final ObservableList<Issue> masterData = FXCollections.observableArrayList();
    @FXML
    private TableView<Issue> issueTable;
    @FXML
    private Button visualizzaAllegatoButton;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private VBox colonnaSinistra;
    @FXML
    private ChoiceBox<String> filtroChoiceBox;
    @FXML
    private ChoiceBox<String> ordinaChoiceBox;


    /**
     * Metodo invocato automaticamente da JavaFX al termine del caricamento del file FXML.
     * Prepara la tabella applicando la formattazione e i filtri previsti esclusivamente
     * per il ruolo EXTERNAL_USER. Collega inoltre la selezione delle righe all'aggiornamento della vista di dettaglio.
     */
    @FXML
    public void initialize() {
        IssueTableHelper.configuraTabella(issueTable, Ruolo.EXTERNAL_USER, false);
        FiltroEOrdinaHelper.configuraFiltroEOrdine(issueTable, masterData, filtroChoiceBox, ordinaChoiceBox, Ruolo.EXTERNAL_USER);

        issueTable.getSelectionModel().selectedItemProperty().addListener((_, _, newValue) -> {
            if(newValue != null) {
                descriptionArea.setText(newValue.getDescrizione());
                visualizzaAllegatoButton.setDisable(newValue.getAllegato()==null);
            }
            else {
                descriptionArea.setText("");
                visualizzaAllegatoButton.setDisable(true);
            }
        });
    }


    /**
     * Gestisce il click sul pulsante di caricamento delle issue.
     * Anima l'ingresso del pannello principale e richiede al backend la lista
     * aggiornata delle issue, delegando il fetch all'apposito helper.
     */
    @FXML
    protected void onElencoBugButtonClick() {
        Stage stage = (Stage) colonnaSinistra.getScene().getWindow();
        if(Validator.sessionInvalid(stage)) return;

        VBoxVisibility.visibility(colonnaSinistra, null, () ->
                Validator.backEndValidator(() -> IssueDataLoader.loadOnTable(Ruolo.EXTERNAL_USER, masterData, false)));
    }


    /**
     * Recupera l'allegato associato alla issue attualmente selezionata in tabella
     * e apre un pop-up dedicato per visualizzare l'immagine ingrandita.
     */
    @FXML
    protected void onVisualizzaAllegatoButtonClick() {
        Stage stage = (Stage) colonnaSinistra.getScene().getWindow();
        if(Validator.sessionInvalid(stage)) return;

        Issue issue = issueTable.getSelectionModel().getSelectedItem();
        Window mainWindow = visualizzaAllegatoButton.getScene().getWindow();
        WindowHelper.apriAllegato(issue, mainWindow);
    }

    /**
     * Invalida la sessione dell'utente corrente e reindirizza l'applicazione alla schermata di login.
     */
    @FXML
    protected void onLogoutButtonClick() {

        AuthSession.getInstance().clearSession();
        Stage stage = (Stage) logoutButton.getParentPopup().getOwnerWindow();
        WindowHelper.tornaAlLogin(stage);
    }

    /**
     * Apre la finestra per permettere all'utente esterno di aggiornare la propria password.
     */
    @FXML
    protected void onCambioPasswordButtonClick() {
        Stage stage = (Stage) colonnaSinistra.getScene().getWindow();
        if(Validator.sessionInvalid(stage)) return;

        CambioPasswordDialog cambioPassword = new CambioPasswordDialog();
        cambioPassword.mostra();
    }

}