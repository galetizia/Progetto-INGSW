package org.example.bugboard26frontend;

import client.AuthClient;
import client.IssueClient;
import enums.Ruolo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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




}
