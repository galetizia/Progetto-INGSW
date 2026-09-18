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


    @FXML
    protected void onElencoBugButtonClick() {
        Stage stage = (Stage) colonnaSinistra.getScene().getWindow();
        if(Validator.sessionInvalid(stage)) return;

        VBoxVisibility.visibility(colonnaSinistra, null, () ->
                Validator.backEndValidator(() -> IssueDataLoader.loadOnTable(Ruolo.EXTERNAL_USER, masterData, false)));
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
    protected void onCambioPasswordButtonClick() {
        Stage stage = (Stage) colonnaSinistra.getScene().getWindow();
        if(Validator.sessionInvalid(stage)) return;

        CambioPasswordDialog cambioPassword = new CambioPasswordDialog();
        cambioPassword.mostra();
    }

}