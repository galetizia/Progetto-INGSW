package org.example.bugboard26frontend;

import client.AuthClient;
import client.IssueClient;
import enums.Ruolo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.AuthUser;

import java.util.List;

public class GestioneUtentiController {

    AuthClient authClient = new AuthClient();
    IssueClient issueClient = new IssueClient();
    private ObservableList<AuthUser> masterData = FXCollections.observableArrayList();
    private FilteredList<AuthUser> filteredData;

    @FXML
    private TableView<AuthUser> utentiTable;
    @FXML
    private TableColumn<AuthUser, String> emailColumn;
    @FXML
    private TableColumn<AuthUser, Ruolo> ruoloColumn;
    @FXML
    private TableColumn<AuthUser, Boolean> statoAccountColumn;

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
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        ruoloColumn.setCellValueFactory(new PropertyValueFactory<>("ruolo"));
        statoAccountColumn.setCellValueFactory(new PropertyValueFactory<>("statoAccount"));

        filteredData = new FilteredList<>(masterData, p -> true);
        utentiTable.setItems(filteredData);

        filtroChoiceBox.getItems().addAll("Tutti", "Attivi", "Non Attivi");
        filtroChoiceBox.setValue("Tutti");

        filtroChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newValue) -> {
            applicaFiltro();
        });
        utentiTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        utentiTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                cambiaStatoButton.setDisable(false);
                if (newSelection.getStatoAccount()) {
                    cambiaStatoButton.setText("Disattiva Utente");
                } else {
                    cambiaStatoButton.setText("Attiva Utente");
                }
            } else {
                cambiaStatoButton.setDisable(true);
                cambiaStatoButton.setText("Disattiva Utente");
            }
        });
        loadOnTable();
    }

    private void applicaFiltro() {
        if(filteredData == null) return;

        String filtro = filtroChoiceBox.getValue();

        filteredData.setPredicate(user -> {
            if ("Attivi".equals(filtro)) return user.getStatoAccount();
            if("Non Attivi".equals(filtro)) return !user.getStatoAccount();
            return true;
        });
    }

    @FXML
    protected void onGestioneUtentiButtonClick(){
        boolean isVisible = colonnaGestione.isVisible();

        if(!isVisible){
            loadOnTable();
            colonnaGestione.setVisible(true);
            colonnaGestione.setManaged(true);
        } else {
            colonnaGestione.setVisible(false);
            colonnaGestione.setManaged(false);
        }

        Stage stage = (Stage) colonnaGestione.getScene().getWindow();
        javafx.application.Platform.runLater(() -> {
            stage.sizeToScene();
            stage.centerOnScreen();
        });
    }

    private void loadOnTable() {
        List<AuthUser> users = authClient.getUsers();
        masterData.setAll(users);
    }

    @FXML
    protected void onCreaUtenteButtonClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("creazione-utente-view.fxml"));
            Parent root = fxmlLoader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Creazione Utente");
            dialogStage.setScene(new javafx.scene.Scene(root));
            dialogStage.setResizable(false);

            // Blocca la finestra sottostante finché il pop-up non viene chiuso
            dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            Stage mainWindow = (Stage) creaUtenteButton.getScene().getWindow();
            dialogStage.initOwner(mainWindow);

            dialogStage.showAndWait(); // Aspetta che il pop-up si chiuda

            // Appena il pop-up si chiude, ricarichiamo la tabella per mostrare il nuovo utente!
            loadOnTable();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Errore nell'apertura del pop-up di creazione utente.");
        }
    }

    @FXML
    protected void onCambiaStatoButtonClick() {
        AuthUser userSelezionato = utentiTable.getSelectionModel().getSelectedItem();

        if (userSelezionato != null) {
            System.out.println("Richiesta cambio stato per utente ID: " + userSelezionato.getId());

            boolean success = authClient.cambiaStatoUtente(userSelezionato.getId());

            if (success) {
                loadOnTable();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Errore");
                alert.setHeaderText(null);
                alert.setContentText("Impossibile cambiare lo stato dell'utente.");
                alert.showAndWait();
            }
        }
    }

    @FXML
    protected void onIndietroButtonClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("admin-home-view.fxml"));
            Parent root = fxmlLoader.load();

            Stage stage = (Stage) indietroButton.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
            stage.setTitle("Admin Dashboard");
            stage.show();
            stage.sizeToScene();
            stage.centerOnScreen();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Errore nel tornare alla schermata Admin.");
        }
    }

}
