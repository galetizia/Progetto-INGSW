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

    private void configuraListenerSelezione(){
        issueTable.getSelectionModel().selectedItemProperty().addListener((_, _, newValue) ->
                aggiornaBottoni(newValue));
    }

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


    @FXML
    public void onElencoIssueButtonClick(){
        Stage stage = (Stage) colonnaSinistra.getScene().getWindow();
        if(Validator.sessionInvalid(stage)) return;

        VBoxVisibility.visibility(colonnaSinistra, colonnaDestra, () ->
                Validator.backEndValidator(() -> IssueDataLoader.loadOnTable(Ruolo.INTERNAL_USER, masterData, false)));
    }


    @FXML
    protected void onBugArchiviatiButtonClick() {
        Stage stage = (Stage) colonnaSinistra.getScene().getWindow();
        if(Validator.sessionInvalid(stage)) return;

        VBoxVisibility.visibility(colonnaDestra, colonnaSinistra, () ->
                Validator.backEndValidator(() -> IssueDataLoader.loadOnTable(Ruolo.INTERNAL_USER, masterDataArchiviate, true)));
    }


    @FXML
    protected void onSegnalaIssueButtonClick(){
        Stage stage = (Stage) colonnaSinistra.getScene().getWindow();
        if(Validator.sessionInvalid(stage)) return;

        WindowHelper.apriSegnalazione(() -> Validator.backEndValidator(() -> IssueDataLoader.loadOnTable(Ruolo.INTERNAL_USER, masterData, false)));
    }


    @FXML
    protected void onVisualizzaAllegatoButtonClick() {
        Stage stage = (Stage) colonnaSinistra.getScene().getWindow();
        if(Validator.sessionInvalid(stage)) return;

        Issue issue = issueTable.getSelectionModel().getSelectedItem();
        Window mainWindow = visualizzaAllegatoButton.getScene().getWindow();
        WindowHelper.apriAllegato(issue, mainWindow);
    }


    @FXML
    protected void onLogoutButtonClick() {
        AuthSession.getInstance().clearSession();
        Stage stage = (Stage) logoutButton.getParentPopup().getOwnerWindow();
        WindowHelper.tornaAlLogin(stage);
    }


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

    @FXML
    protected void onCambioPasswordButtonClick(){
        Stage stage = (Stage) colonnaSinistra.getScene().getWindow();
        if(Validator.sessionInvalid(stage)) return;

        CambioPasswordDialog cambioPassword = new CambioPasswordDialog();
        cambioPassword.mostra();
    }

}
