package org.example.bugboard26frontend.controller;

import client.AuthSession;
import client.IssueClient;
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
 * Controller della schermata dedicata all'Admin.
 * Gestisce l'interfaccia utente per la visualizzazione delle issue (sia attive chè archiviate),
 * l'interazione con le tabelle, l'eliminazione dei duplicati e la navigazione verso gli altri pannelli.
 */
public class AdminHomeController {
    @FXML
    private Button segnalaComeDuplicatoButton;
    @FXML
    private Button visualizzaAllegatoButton;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private TableView<Issue> archiviatiTable;
    @FXML
    private TableView<Issue> issueTable;
    @FXML
    private VBox colonnaSinistra;
    @FXML
    private VBox colonnaDestra;
    @FXML
    private Button archiviaIssueButton;
    @FXML
    private ChoiceBox<String> filtroChoiceBox;
    @FXML
    private ChoiceBox<String> ordinaChoiceBox;

    private final ObservableList<Issue> masterData = FXCollections.observableArrayList();
    private final ObservableList<Issue> masterDataArchiviate = FXCollections.observableArrayList();

    private IssueClient issueClient;

    /**
     * Passa il client per le chiamate HTTP al backend.
     * Necessario per disaccoppiare la logica di rete dalla classe grafica.
     *
     * @param issueClient L'istanza configurata del client delle issue.
     */
    public void setIssueClient(IssueClient issueClient) {
        this.issueClient = issueClient;
    }

    /**
     * Metodo invocato automaticamente dal runtime di JavaFX al termine del caricamento del file FXML.
     * Inizializza l'aspetto delle tabelle, applica i filtri per il ruolo Admin e imposta i listener
     * di selezione per abilitare/disabilitare i pulsanti d'azione in base alla riga cliccata.
     */
    @FXML
    public void initialize()
    {
        issueTable.setItems(masterData);
        archiviatiTable.setItems(masterDataArchiviate);

        IssueTableHelper.configuraTabella(issueTable, Ruolo.ADMIN, false);

        IssueTableHelper.configuraTabella(archiviatiTable, Ruolo.ADMIN, true);

        FiltroEOrdinaHelper.configuraFiltroEOrdine(issueTable, masterData, filtroChoiceBox, ordinaChoiceBox, Ruolo.ADMIN);


        issueTable.getSelectionModel().selectedItemProperty().addListener((_, _, newValue) -> {
            if(newValue != null) {
                descriptionArea.setText(newValue.getDescrizione());
                visualizzaAllegatoButton.setDisable(newValue.getAllegato()==null);
                if(archiviaIssueButton != null) archiviaIssueButton.setDisable(false);
                segnalaComeDuplicatoButton.setDisable(false);
            }
            else {
                descriptionArea.setText("");
                visualizzaAllegatoButton.setDisable(true);
                if(archiviaIssueButton != null) archiviaIssueButton.setDisable(true);
                segnalaComeDuplicatoButton.setDisable(true);
            }
        });
    }

    /**
     * Gestisce il click sul pulsante per visualizzare le issue attive.
     * Alterna la visibilità dei pannelli nascondendo l'archivio e
     * innesca il caricamento protetto dei dati aggiornati dal server.
     */
    @FXML
    public void onElencoIssueButtonClick(){
        Stage stage = (Stage) colonnaSinistra.getScene().getWindow();
        if(Validator.sessionInvalid(stage)) return;

        VBoxVisibility.visibility(colonnaSinistra, colonnaDestra, () ->
            Validator.backEndValidator(() -> IssueDataLoader.loadOnTable(Ruolo.ADMIN, masterData, false)));
    }


    /**
     * Gestisce il click sul pulsante dell'Archivio.
     * Mostra la tabella dedicata allo storico delle issue chiuse e innesca la chiamata di rete
     * per scaricare i dati archiviati.
     */
    @FXML
    protected void onArchivioBugButtonClick() {
        Stage stage = (Stage) colonnaSinistra.getScene().getWindow();
        if(Validator.sessionInvalid(stage)) return;

        VBoxVisibility.visibility(colonnaDestra, colonnaSinistra, () ->
                Validator.backEndValidator(() -> IssueDataLoader.loadOnTable(Ruolo.ADMIN, masterDataArchiviate, true)));
    }


    /**
     * Apre la finestra modale per permettere all'admin di aggiornare la propria password.
     */
    @FXML
    protected void onCambioPasswordButtonClick(){
        Stage stage = (Stage) colonnaSinistra.getScene().getWindow();
        if(Validator.sessionInvalid(stage)) return;

        CambioPasswordDialog cambioPassword = new CambioPasswordDialog();
        cambioPassword.mostra();

    }


    /**
     * Richiama l'helper per aprire la finestra di creazione di una nuova issue.
     * Configura una callback affinché, alla chiusura del modulo, la tabella delle issue
     * si ricarichi automaticamente per mostrare la nuova issue appena inserita.
     */
    public void onSegnalaIssueButtonClick(){
        Stage stage = (Stage) colonnaSinistra.getScene().getWindow();
        if(Validator.sessionInvalid(stage)) return;

        WindowHelper.apriSegnalazione(() -> Validator.backEndValidator(() -> IssueDataLoader.loadOnTable(Ruolo.ADMIN, masterData, false)));
    }


    /**
     * Estrae l'allegato dalla issue selezionata e apre una finestra dedicata
     * per visualizzare l'immagine ingrandita a schermo.
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
     * Invalida la sessione dell'utente corrente e riporta l'applicazione alla schermata iniziale di login.
     */
    @FXML
    protected void onLogoutButtonClick() {
        Stage stage = (Stage) colonnaSinistra.getScene().getWindow();
        if(Validator.sessionInvalid(stage)) return;

        AuthSession.getInstance().clearSession();
        WindowHelper.tornaAlLogin(stage);
    }


    /**
     * Delega all'helper la logica di archiviazione della issue selezionata in tabella.
     * In caso di successo, richiede al backend i dati aggiornati per sincronizzare sia
     * la tabella delle issue attive che quella dell'archivio, azzerando poi la selezione.
     */
    @FXML
    public void handleArchiviaIssue() {
        Stage stage = (Stage) colonnaSinistra.getScene().getWindow();
        if(Validator.sessionInvalid(stage)) return;

        Issue issueSelezionata = issueTable.getSelectionModel().getSelectedItem();

        Validator.backEndValidator(() ->
                IssueActionHandler.archivia(issueSelezionata, () -> {
                    IssueDataLoader.loadOnTable(Ruolo.ADMIN, masterData, false);
                    if (colonnaDestra.isVisible()) {
                        IssueDataLoader.loadOnTable(Ruolo.ADMIN, masterDataArchiviate, true);
                    }
                    archiviaIssueButton.setDisable(true);
                    javafx.application.Platform.runLater(() -> issueTable.requestFocus());
                })
        );
    }

    /**
     * Intercetta la richiesta di eliminazione di un duplicato.
     * Essendo un'operazione critica, mostra prima un Alert visivo per chiedere
     * una doppia conferma. Se accettata, elimina l'elemento sia dal database che dalla tabella local.
     */
    @FXML
    protected void onSegnalaComeDuplicatoButtonClick(){
        Stage stage = (Stage) colonnaSinistra.getScene().getWindow();
        if(Validator.sessionInvalid(stage)) return;

        Validator.backEndValidator(() -> {
            Issue issue = issueTable.getSelectionModel().getSelectedItem();

            if (issue == null) return;

            Alert confirmDelete = MyAlert.mostraAlertConfirmation(
                    "Conferma eliminazione",
                    "Segnalazione Issue",
                    "Sei sicuro di voler segnalare la issue come duplicata?\n\nATTENZIONE: Questa operazione eliminerà definitivamente la issue e non potrà essere recuperata.");

            confirmDelete.showAndWait().ifPresent(response -> {
                if(response == ButtonType.OK){

                    boolean success = issueClient.eliminaIssue(issue.getId());
                    if(success){
                        masterData.remove(issue);
                        segnalaComeDuplicatoButton.setDisable(true);
                        archiviaIssueButton.setDisable(true);
                        visualizzaAllegatoButton.setDisable(true);

                        MyAlert.mostraAlert(Alert.AlertType.INFORMATION, "Successo", "Issue eliminata con successo.");
                    } else {
                        MyAlert.mostraAlert(Alert.AlertType.ERROR, "Errore", "Impossibile eliminare issue. Verifica la connessione al server.");
                    }
                }
                javafx.application.Platform.runLater(() -> issueTable.requestFocus());
            });
        });
    }

    /**
     * Sostituisce la vista corrente con la schermata dedicata all'amministrazione degli utenti.
     */
    @FXML
    protected void onGestioneUtentiButtonClick(){
        Stage stage = (Stage) colonnaSinistra.getScene().getWindow();
        if(Validator.sessionInvalid(stage)) return;

        WindowHelper.apriGestioneUtenti(stage);
    }
}
