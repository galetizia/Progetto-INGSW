package org.example.bugboard26frontend;

import client.AuthClient;
import client.IssueClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import client.AuthSession;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Issue;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class UserHomeController {
    @FXML
    private MenuItem logoutButton;

    @FXML
    private Button elencoButton;

    AuthClient authClient = new AuthClient();
    IssueClient issueClient = new IssueClient();

    @FXML
    private TableView<Issue> issueTable;

    @FXML
    private StackPane contentArea;
    @FXML
    private TableColumn<Issue, Integer> idColumn;
    @FXML
    private TableColumn<Issue, String> titoloColumn;
    @FXML
    private TableColumn<Issue, String> statoColumn;
    @FXML
    private TableColumn<Issue, String> prioritaColumn;
    @FXML
    private TableColumn<Issue, String> dataColumn;

    @FXML
    public void initialize()
    {
        contentArea.setVisible(false);
    }
    @FXML
    protected void onSegnalaIssueButtonClick(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("segnalazione-issue-view.fxml"));
            Parent root = fxmlLoader.load();

            // nuova finestra(pop-up)
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Segnalazione");
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(false);

            // per bloccare le finestre sottostanti
            dialogStage.initModality(Modality.APPLICATION_MODAL);

            // recuperiamo finestra principale
            Stage mainWindow = (Stage) logoutButton.getParentPopup().getOwnerWindow();
            dialogStage.initOwner(mainWindow);

            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Errore nell'apertura finestra segnalazione");
        }
    }

    @FXML
    protected void onElencoIssueButtonClick(){
        if(issueTable.getItems().isEmpty()){
            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            titoloColumn.setCellValueFactory(new PropertyValueFactory<>("titolo"));
            statoColumn.setCellValueFactory(new PropertyValueFactory<>("stato"));
            prioritaColumn.setCellValueFactory(new PropertyValueFactory<>("priorita"));
            dataColumn.setCellValueFactory(new PropertyValueFactory<>("data"));

            loadOnTable();
            contentArea.setVisible(true);
        } else{
            issueTable.getItems().clear();
            contentArea.setVisible(false);
        }
    }

    private void loadOnTable() {
        List<Issue> issues = issueClient.elencoIssue();
        ObservableList<Issue> observableList = FXCollections.observableArrayList(issues);
        issueTable.setItems(observableList);
    }

    @FXML
    protected void onLogoutButtonClick() {
        authClient.logout();

        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login-view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) logoutButton.getParentPopup().getOwnerWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e){
            e.printStackTrace();
            System.out.println("Errore nell'apertura schermata login");
        }
    }

    @FXML
    protected void onCambioPasswordButtonClick(){

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Cambio Password");
        dialog.setHeaderText("Inserire i dati per il cambio password");

        ButtonType confermaButton = new ButtonType("Conferma",  ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confermaButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        PasswordField oldPasswordField = new PasswordField();
        oldPasswordField.setPromptText("Password attuale");

        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("Nuova Password");

        grid.add(new Label("Email:"), 0, 0);
        grid.add(emailField, 1, 0);
        grid.add(new Label("Password Attuale:"), 0, 1);
        grid.add(oldPasswordField, 1, 1);
        grid.add(new Label("Nuova Password:"), 0, 2);
        grid.add(newPasswordField, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.showAndWait().ifPresent(response -> {
            if (response == confermaButton) {
                String email = emailField.getText();
                String oldPassword = oldPasswordField.getText();
                String newPassword = newPasswordField.getText();

                boolean success = authClient.changePassword(email, oldPassword, newPassword);

                // da implementare il controllo della password
                if (success){
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Successo");
                    alert.setHeaderText(null);
                    alert.setContentText("Password cambiata con successo");
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Errore");
                    alert.setHeaderText(null);
                    alert.setContentText("Errore nel cambio password, ricontrollare i dati");
                    alert.showAndWait();
                }
            }
        });
    }

}
