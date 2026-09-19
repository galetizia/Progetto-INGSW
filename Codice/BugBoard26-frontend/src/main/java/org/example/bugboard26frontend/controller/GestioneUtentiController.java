package org.example.bugboard26frontend.controller;

import client.AuthClient;
import client.IssueClient;
import enums.Ruolo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.AuthUser;
import org.example.bugboard26frontend.helper.*;

import java.util.Map;

/**
 * Controller del pannello di amministrazione degli utenti da parte dell'admin.
 * Gestisce due viste principali :
 * 1) La gestione degli utenti (creazione, attivazione, disattivazione).
 * 2) La dashboard analitica con i grafici statistici globali del sistema.
 */
public class GestioneUtentiController {

    private final ObservableList<AuthUser> masterData = FXCollections.observableArrayList();
    @FXML
    private TableView<AuthUser> utentiTable;
    @FXML
    private PieChart bugChart;
    @FXML
    private BarChart<String, Number> bugPerUserChart;
    @FXML
    private VBox colonnaDashboard;
    @FXML
    private VBox colonnaGestione;
    @FXML
    private ChoiceBox<String> filtroChoiceBox;
    @FXML private Button cambiaStatoButton;
    @FXML private Button indietroButton;

    private IssueClient issueClient;
    private AuthClient authClient;

    public void setIssueClient(IssueClient issueClient) {
        this.issueClient = issueClient;
    }
    public void setAuthClient(AuthClient authClient) {
        this.authClient = authClient;
    }

    /**
     * Metodo invocato automaticamente da JavaFX al termine del caricamento del file FXML.
     * Inizializza la tabella degli utenti, imposta i filtri visivi e aggancia i listener di selezione.
     */
    @FXML void initialize() {
        UserTableHelper.configuraTabella(utentiTable);
        FiltroEOrdinaHelper.configuraFiltroUtenti(utentiTable, masterData, filtroChoiceBox);

        configuraListenerSelezione();
    }

    /**
     * Associa un listener alla tabella degli utenti per intercettare i click sulle righe.
     * Quando un utente viene selezionato, aggiorna dinamicamente lo stato dei bottoni operativi.
     */
    private void configuraListenerSelezione() {
        utentiTable.getSelectionModel().selectedItemProperty().addListener((_, _, newSelection) ->
            aggiornaBottoni(newSelection));
    }

    /**
     * Aggiorna la disponibilità e il testo del pulsante "Cambia Stato" in base all'utente selezionato.
     * Se l'account è attivo, il pulsante proporrà la disattivazione e viceversa.
     *
     * @param user L'utente attualmente selezionato nella tabella.
     */
    private void aggiornaBottoni(AuthUser user) {
        if (user != null) {
            cambiaStatoButton.setDisable(false);
            if (user.getStatoAccount()) {
                cambiaStatoButton.setText("Disattiva Utente");
            } else {
                cambiaStatoButton.setText("Attiva Utente");
            }
        } else {
            cambiaStatoButton.setDisable(true);
            cambiaStatoButton.setText("Disattiva Utente");
        }
    }


    /**
     * Popola il grafico a torta analizzando lo stato di avanzamento delle issue attive.
     * Richiede i dati al server e li delega al DiagramDataLoader per la formattazione grafica.
     */
    @FXML
    protected void onStatoIssueButtonClick(){
        Stage stage = (Stage) indietroButton.getScene().getWindow();
        if(Validator.sessionInvalid(stage)) return;

        Validator.backEndValidator(() -> {
            Map<String, Integer> dataStates = issueClient.countIssueStates();
            ObservableList<PieChart.Data> issueStates = DiagramDataLoader.configuraDiagrammaStatoIssueAttive(dataStates);
            bugChart.setData(issueStates);
            bugChart.setTitle("Stato Issue Attive");
        });
    }


    /**
     * Popola il grafico a torta raggruppando le issue per tipologia.
     */
    @FXML
    protected void onTipoIssueButtonClick(){
        Stage stage = (Stage) indietroButton.getScene().getWindow();
        if(Validator.sessionInvalid(stage)) return;

        Validator.backEndValidator(() -> {
            Map<String, Integer> issuesType = issueClient.countIssueTypes();
            ObservableList<PieChart.Data> issueTypes = DiagramDataLoader.configuraDiagrammaTipoIssueAttive(issuesType);
            bugChart.setData(issueTypes);
            bugChart.setTitle("Tipologia Issue");
        });
    }

    /**
     * Metodo di supporto che inizializza la dashboard richiamando i grafici di default
     * non appena il pannello delle statistiche viene reso visibile.
     */
    private void popolaDashboard() {
        onStatoIssueButtonClick();
        onIssueAssegnateButtonClick();
    }


    /**
     * Popola il grafico a barre mostrando la media dei tempi di risoluzione per i vari utenti.
     * Pulisce prima gli assi per evitare sovrapposizioni grafiche con dati precedenti.
     */
    @FXML
    protected void onTempoButtonClick() {
        Stage stage = (Stage) indietroButton.getScene().getWindow();
        if(Validator.sessionInvalid(stage)) return;

        Validator.backEndValidator(() -> {
            ((javafx.scene.chart.CategoryAxis) bugPerUserChart.getXAxis()).getCategories().clear();
            bugPerUserChart.getData().clear();

            Map<String, Double> dataTimes = authClient.getTimePerUser();

            String textMedia = DiagramDataLoader.calcoloTempoMedio(dataTimes);
            XYChart.Series<String, Number> series = DiagramDataLoader.preparaDatiTempoMedio(dataTimes);

            DiagramDataLoader.configuraDiagrammaTempoMedio(bugPerUserChart, textMedia);

            bugPerUserChart.getData().add(series);
        });
    }

    /**
     * Popola il grafico a barre mostrando la classifica degli utenti con il maggior numero di issue assegnate.
     */
    @FXML
    protected void onIssueAssegnateButtonClick(){
        Stage stage = (Stage) indietroButton.getScene().getWindow();
        if(Validator.sessionInvalid(stage)) return;

        Validator.backEndValidator(() -> {
            Map<String, Integer> issuesPerUser = authClient.getIssuesPerUser();

            XYChart.Series<String, Number> series = DiagramDataLoader.preparaDatiIssueAssegnate(issuesPerUser);

            DiagramDataLoader.configuraDiagrammaIssueAssegnate(bugPerUserChart);

            ((javafx.scene.chart.CategoryAxis) bugPerUserChart.getXAxis()).getCategories().clear();
            bugPerUserChart.getData().clear();
            bugPerUserChart.getData().add(series);
        });
    }

    /**
     * Gestisce la navigazione interna verso la visualizzazione della Dashboard analitica.
     * Utilizza l'helper per invertire la visibilità dei pannelli e innesca il caricamento dei grafici.
     */
    @FXML
    protected void onVisualizzaDashboardButtonClick() {
        Stage stage = (Stage) indietroButton.getScene().getWindow();
        if(Validator.sessionInvalid(stage)) return;

        VBoxVisibility.visibility(colonnaDashboard, colonnaGestione, this::popolaDashboard);
    }

    /**
     * Gestisce la navigazione interna verso la tabella di gestione anagrafica degli utenti.
     * Utilizza l'helper per invertire la visibilità dei pannelli e scarica dal server la lista degli account.
     */
    @FXML
    protected void onGestioneUtentiButtonClick(){
        Stage stage = (Stage) indietroButton.getScene().getWindow();
        if(Validator.sessionInvalid(stage)) return;

        VBoxVisibility.visibility(colonnaGestione, colonnaDashboard, () ->
                Validator.backEndValidator(() -> UserDataLoader.loadUserData(masterData)));
    }

    /**
     * Apre la finestra per la creazione di un nuovo utente.
     * Al termine, aggiorna automaticamente la tabella per mostrare il nuovo account.
     */
    @FXML
    protected void onCreaUtenteButtonClick() {
        Stage stage = (Stage) indietroButton.getScene().getWindow();
        if(Validator.sessionInvalid(stage)) return;

        WindowHelper.apriCreazioneUtente(stage, () -> Validator.backEndValidator(() -> UserDataLoader.loadUserData(masterData)));
    }

    /**
     * Invia al server la richiesta per invertire lo stato dell'account selezionato, da attivo a disattivo e viceversa.
     * Una volta completata l'operazione, ricarica i dati nella tabella.
     */
    @FXML
    protected void onCambiaStatoButtonClick() {
        Stage stage = (Stage) indietroButton.getScene().getWindow();
        if(Validator.sessionInvalid(stage)) return;

        AuthUser userSelezionato = utentiTable.getSelectionModel().getSelectedItem();
        if(userSelezionato == null) return;

        Validator.backEndValidator(() ->
                UserActionHandler.cambiaStatoAccount(userSelezionato, () -> UserDataLoader.loadUserData(masterData)));
    }

    /**
     * Chiude il pannello di gestione utenti e riporta l'admin alla propria schermata principale.
     */
    @FXML
    protected void onIndietroButtonClick() {
        Stage stage = (Stage) indietroButton.getScene().getWindow();
        if(Validator.sessionInvalid(stage)) return;
        WindowHelper.apriHome(stage, Ruolo.ADMIN);
    }
}
