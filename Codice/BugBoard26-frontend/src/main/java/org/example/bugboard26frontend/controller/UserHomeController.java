package org.example.bugboard26frontend.controller;

import enums.Ruolo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import client.AuthSession;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import model.Issue;
import org.example.bugboard26frontend.helper.*;

/**
 * Controller della schermata principale dedicata all'Internal User.
 * Rappresenta l'area di lavoro per gli operatori, permette di visualizzare le issue attive,
 * prenderle in carico, segnarle come risolte, rilasciarle e consultare l'archivio.
 */
public class UserHomeController {
    @FXML
    private MenuItem logoutButton;
    private final ObservableList<Issue> masterData = FXCollections.observableArrayList();
    private final ObservableList<Issue> masterDataArchiviate = FXCollections.observableArrayList();
    @FXML
    private TableView<Issue> issueTable;
    @FXML
    private Button visualizzaAllegatoButton;
    @FXML
    private Button prendiInCaricoButton;
    @FXML
    private Button rilasciaIssueButton;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private TableView<Issue> archiviatiTable;
    @FXML
    private VBox colonnaSinistra;
    @FXML
    private VBox colonnaDestra;
    @FXML
    private ChoiceBox<String> filtroChoiceBox;
    @FXML
    private ChoiceBox<String> ordinaChoiceBox;

    /**
     * Metodo invocato automaticamente da JavaFX al termine del caricamento del file FXML.
     * Inizializza le tabelle applicando i filtri di visibilità specifici
     * per il ruolo INTERNAL_USER e configura i listener di interazione sulle righe.
     */
    @FXML
    public void initialize()
    {
        issueTable.setItems(masterData);
        archiviatiTable.setItems(masterDataArchiviate);

        IssueTableHelper.configuraTabella(issueTable, Ruolo.INTERNAL_USER, false);

        IssueTableHelper.configuraTabella(archiviatiTable, Ruolo.INTERNAL_USER, true);

        FiltroEOrdinaHelper.configuraFiltroEOrdine(issueTable, masterData, filtroChoiceBox, ordinaChoiceBox, Ruolo.INTERNAL_USER);

        configuraListenerSelezione();
    }

    /**
     * Associa un listener alla tabella delle issue per intercettare i cambi di selezione dell'utente
     * e aggiornare in tempo reale la vista di dettaglio e i bottoni operativi.
     */
    private void configuraListenerSelezione(){
        issueTable.getSelectionModel().selectedItemProperty().addListener((_, _, newValue) ->
                aggiornaBottoni(newValue));
    }

    /**
     * Logica di User Experience (UX) che adatta dinamicamente l'interfaccia in base alla issue selezionata.
     * Cambia il testo del bottone ("Prendi in carico" e "Segna come Risolto") e
     * abilita/disabilita azioni come il rilascio in base allo stato della issue e al fatto che sia
     * assegnata o meno all'utente attualmente loggato.
     *
     * @param issue La issue selezionata dall'utente.
     */
    private void aggiornaBottoni(Issue issue){
        if(issue == null) {
            descriptionArea.setText("");
            visualizzaAllegatoButton.setDisable(true);
            prendiInCaricoButton.setText("Prendi in carico");
            prendiInCaricoButton.setDisable(true);
            if (rilasciaIssueButton != null) rilasciaIssueButton.setDisable(true);
            return;
        }

        descriptionArea.setText(issue.getDescrizione());
        visualizzaAllegatoButton.setDisable(issue.getAllegato() == null);

        boolean isToDo = "TO_DO".equalsIgnoreCase(issue.getStato());
        boolean isInLavorazione = "ASSEGNATO".equalsIgnoreCase(issue.getStato())
                && issue.getAssignee() != null
                && issue.getAssignee().getId() == AuthSession.getInstance().getUtenteCorrente().getId();

        if (isToDo) {
            prendiInCaricoButton.setText("Prendi in carico");
            prendiInCaricoButton.setDisable(false);
            if (rilasciaIssueButton != null) rilasciaIssueButton.setDisable(true);
        } else if (isInLavorazione) {

            prendiInCaricoButton.setText("Segna come Risolto");
            prendiInCaricoButton.setDisable(false);
            if (rilasciaIssueButton != null) rilasciaIssueButton.setDisable(false);

        } else {
            prendiInCaricoButton.setText("Prendi in carico");
            prendiInCaricoButton.setDisable(true);
            if (rilasciaIssueButton != null) rilasciaIssueButton.setDisable(true);
        }
    }


    /**
     * Alterna la visibilità dei pannelli per mostrare l'elenco issue attive
     * e richiede l'aggiornamento dei dati tramite l'helper.
     */
    @FXML
    public void onElencoIssueButtonClick(){
        Stage stage = (Stage) colonnaSinistra.getScene().getWindow();
        if(Validator.sessionInvalid(stage)) return;

        VBoxVisibility.visibility(colonnaSinistra, colonnaDestra, () ->
                Validator.backEndValidator(() -> IssueDataLoader.loadOnTable(Ruolo.INTERNAL_USER, masterData, false)));
    }


    /**
     * Alterna la visibilità dei pannelli per mostrare l'archivio delle issue risolte o chiuse,
     * effettuando il caricamento dei dati specifici.
     */
    @FXML
    protected void onBugArchiviatiButtonClick() {
        Stage stage = (Stage) colonnaSinistra.getScene().getWindow();
        if(Validator.sessionInvalid(stage)) return;

        VBoxVisibility.visibility(colonnaDestra, colonnaSinistra, () ->
                Validator.backEndValidator(() -> IssueDataLoader.loadOnTable(Ruolo.INTERNAL_USER, masterDataArchiviate, true)));
    }


    /**
     * Apre la finestra per la creazione di una nuova issue.
     * Alla chiusura del pop-up, innesca il ricaricamento della tabella per mostrare il nuovo elemento.
     */
    @FXML
    protected void onSegnalaIssueButtonClick(){
        Stage stage = (Stage) colonnaSinistra.getScene().getWindow();
        if(Validator.sessionInvalid(stage)) return;

        WindowHelper.apriSegnalazione(() -> Validator.backEndValidator(() -> IssueDataLoader.loadOnTable(Ruolo.INTERNAL_USER, masterData, false)));
    }


    /**
     * Estrae l'allegato dalla issue selezionata in tabella e ne comanda la visualizzazione
     * tramite una finestra dedicata.
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
     * Pulisce i dati della sessione corrente e reindirizza l'applicazione alla schermata di login.
     */
    @FXML
    protected void onLogoutButtonClick() {
        AuthSession.getInstance().clearSession();
        Stage stage = (Stage) logoutButton.getParentPopup().getOwnerWindow();
        WindowHelper.tornaAlLogin(stage);
    }


    /**
     * Invoca l'helper per gestire il flusso operativo sulla issue selezionata (presa in carico o risoluzione,
     * a seconda dello stato attuale). Al termine, ricarica la tabella e ripristina il focus.
     */
    @FXML
    protected void prendiInCaricoButtonClick(){
        Stage stage = (Stage) colonnaSinistra.getScene().getWindow();
        if(Validator.sessionInvalid(stage)) return;

        Issue issueSelezionata = issueTable.getSelectionModel().getSelectedItem();
        Validator.backEndValidator(() ->
                IssueActionHandler.prendiInCarico(issueSelezionata, () -> {
                    IssueDataLoader.loadOnTable(Ruolo.INTERNAL_USER, masterData, false);
                    javafx.application.Platform.runLater(() -> issueTable.requestFocus());
                })
        );
    }

    /**
     * Invoca l'helper per completare il rilascio di una issue (rimettendola in stato TO_DO).
     * Al termine, ricarica i dati in tabella per sincronizzare la vista con il backend.
     */
    @FXML
    protected void rilasciaIssueButtonClick() {
        Stage stage = (Stage) colonnaSinistra.getScene().getWindow();
        if(Validator.sessionInvalid(stage)) return;

        Issue issueSelezionata = issueTable.getSelectionModel().getSelectedItem();
        Validator.backEndValidator(() ->
                IssueActionHandler.rilascia(issueSelezionata, () -> {
                    IssueDataLoader.loadOnTable(Ruolo.INTERNAL_USER, masterData, false);
                    javafx.application.Platform.runLater(() -> issueTable.requestFocus());
                })
        );
    }

    /**
     * Apre la finestra di dialogo per permettere all'utente di cambiare la propria password.
     */
    @FXML
    protected void onCambioPasswordButtonClick(){
        Stage stage = (Stage) colonnaSinistra.getScene().getWindow();
        if(Validator.sessionInvalid(stage)) return;

        CambioPasswordDialog cambioPassword = new CambioPasswordDialog();
        cambioPassword.mostra();
    }

}
