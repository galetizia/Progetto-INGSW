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
    public void setIssueClient(IssueClient issueClient) {
        this.issueClient = issueClient;
    }

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

    @FXML
    public void onElencoIssueButtonClick(){
        Stage stage = (Stage) colonnaSinistra.getScene().getWindow();
        if(Validator.sessionInvalid(stage)) return;

        VBoxVisibility.visibility(colonnaSinistra, colonnaDestra, () ->
            Validator.backEndValidator(() -> IssueDataLoader.loadOnTable(Ruolo.ADMIN, masterData, false)));
    }


    @FXML
    protected void onArchivioBugButtonClick() {
        Stage stage = (Stage) colonnaSinistra.getScene().getWindow();
        if(Validator.sessionInvalid(stage)) return;

        VBoxVisibility.visibility(colonnaDestra, colonnaSinistra, () ->
                Validator.backEndValidator(() -> IssueDataLoader.loadOnTable(Ruolo.ADMIN, masterDataArchiviate, true)));
    }


    @FXML
    protected void onCambioPasswordButtonClick(){
        Stage stage = (Stage) colonnaSinistra.getScene().getWindow();
        if(Validator.sessionInvalid(stage)) return;

        CambioPasswordDialog cambioPassword = new CambioPasswordDialog();
        cambioPassword.mostra();

    }



    public void onSegnalaIssueButtonClick(){
        Stage stage = (Stage) colonnaSinistra.getScene().getWindow();
        if(Validator.sessionInvalid(stage)) return;

        WindowHelper.apriSegnalazione(() -> Validator.backEndValidator(() -> IssueDataLoader.loadOnTable(Ruolo.ADMIN, masterData, false)));
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
        Stage stage = (Stage) colonnaSinistra.getScene().getWindow();
        if(Validator.sessionInvalid(stage)) return;

        AuthSession.getInstance().clearSession();
        WindowHelper.tornaAlLogin(stage);
    }


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

    @FXML
    protected void onGestioneUtentiButtonClick(){
        Stage stage = (Stage) colonnaSinistra.getScene().getWindow();
        if(Validator.sessionInvalid(stage)) return;

        WindowHelper.apriGestioneUtenti(stage);
    }
}
