package org.example.bugboard26frontend;

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
import javafx.util.StringConverter;
import jdk.jshell.Diag;
import model.AuthUser;
import org.example.bugboard26frontend.helper.*;

import java.util.Map;

public class GestioneUtentiController {

    AuthClient authClient = new AuthClient();
    IssueClient issueClient = new IssueClient();
    private final ObservableList<AuthUser> masterData = FXCollections.observableArrayList();
    private final MyAlert alert = new MyAlert();

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
    @FXML private Button creaUtenteButton;
    @FXML private Button cambiaStatoButton;
    @FXML private Button indietroButton;

    @FXML void initialize() {
        UserTableHelper.configuraTabella(utentiTable);
        FiltroEOrdinaHelper.configuraFiltroUtenti(utentiTable, masterData, filtroChoiceBox);

        configuraListenerSelezione();
    }


    private void configuraListenerSelezione() {
        utentiTable.getSelectionModel().selectedItemProperty().addListener((_, _, newSelection) ->
            aggiornaBottoni(newSelection));
    }


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


    @FXML
    protected void onStatoIssueButtonClick(){
        Map<String, Integer> dataStates = issueClient.countIssueStates();
        ObservableList<PieChart.Data> issueStates = DiagramDataLoader.configuraDiagrammaStatoIssueAttive(dataStates);
        bugChart.setData(issueStates);
        bugChart.setTitle("Stato Issue Attive");
    }


    @FXML
    protected void onTipoIssueButtonClick(){
        Map<String, Integer> issuesType = issueClient.countIssueTypes();
        ObservableList<PieChart.Data> issueTypes = DiagramDataLoader.configuraDiagrammaTipoIssueAttive(issuesType);
        bugChart.setData(issueTypes);
        bugChart.setTitle("Tipologia Issue");
    }


    private void popolaDashboard() {
        onStatoIssueButtonClick();
        onIssueAssegnateButtonClick();
    }


    @FXML
    protected void onTempoButtonClick() {
        ((javafx.scene.chart.CategoryAxis) bugPerUserChart.getXAxis()).getCategories().clear();
        bugPerUserChart.getData().clear();

        Map<String, Double> dataTimes = authClient.getTimePerUser();

        String textMedia = DiagramDataLoader.calcoloTempoMedio(dataTimes);
        XYChart.Series<String, Number> series = DiagramDataLoader.preparaDatiTempoMedio(dataTimes);

        DiagramDataLoader.configuraDiagrammaTempoMedio(bugPerUserChart, textMedia);

        bugPerUserChart.getData().add(series);
    }

    @FXML
    protected void onIssueAssegnateButtonClick(){
        Map<String, Integer> issuesPerUser = authClient.getIssuesPerUser();

        XYChart.Series<String, Number> series = DiagramDataLoader.preparaDatiIssueAssegnate(issuesPerUser);

        DiagramDataLoader.configuraDiagrammaIssueAssegnate(bugPerUserChart);

        ((javafx.scene.chart.CategoryAxis) bugPerUserChart.getXAxis()).getCategories().clear();
        bugPerUserChart.getData().clear();
        bugPerUserChart.getData().add(series);
    }

    @FXML
    protected void onVisualizzaDashboardButtonClick() {
        VBoxVisibility.visibility(colonnaDashboard, colonnaGestione, this::popolaDashboard);
    }

    @FXML
    protected void onGestioneUtentiButtonClick(){
        VBoxVisibility.visibility(colonnaGestione, colonnaDashboard, () ->
                UserDataLoader.loadUserData(masterData));
    }

    @FXML
    protected void onCreaUtenteButtonClick() {
        Stage stage = (Stage) indietroButton.getScene().getWindow();
        WindowHelper.apriCreazioneUtente(stage, () -> UserDataLoader.loadUserData(masterData));
    }

    @FXML
    protected void onCambiaStatoButtonClick() {
        AuthUser userSelezionato = utentiTable.getSelectionModel().getSelectedItem();
        UserActionHandler.cambiaStatoAccount(userSelezionato, () ->
                UserDataLoader.loadUserData(masterData));
    }

    @FXML
    protected void onIndietroButtonClick() {
            Stage stage = (Stage) indietroButton.getScene().getWindow();
            WindowHelper.apriHome(stage, Ruolo.ADMIN);
    }


}
