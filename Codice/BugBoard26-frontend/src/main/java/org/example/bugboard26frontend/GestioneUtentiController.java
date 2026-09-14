package org.example.bugboard26frontend;

import client.AuthClient;
import client.IssueClient;
import enums.Ruolo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.AuthUser;

import java.util.List;
import java.util.Map;

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
    private PieChart bugChart;
    @FXML
    private BarChart<String, Number> bugPerUserChart;
    @FXML
    private TableColumn<AuthUser, Double> tempoMedioColumn;
    @FXML
    private TableColumn<AuthUser, Integer> issueAttiveColumn;

    @FXML
    private VBox colonnaDashboard;
    @FXML
    private VBox colonnaGestione;
    @FXML
    private ChoiceBox<String> filtroChoiceBox;
    @FXML private Button creaUtenteButton;
    @FXML private Button cambiaStatoButton;
    @FXML private Button indietroButton;
    @FXML private BarChart<String, Number> timePerUserChart;

    @FXML void initialize() {
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        ruoloColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getRuolo()));

        ruoloColumn.setCellFactory(column -> new TableCell<AuthUser, Ruolo>() {
            @Override
            protected void updateItem(Ruolo item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {setText(null);}
                else {String ruolo = item.name().replace("_USER","");
                setText(ruolo);}
            }
        });

        statoAccountColumn.setCellValueFactory(cellData ->
                        new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getStatoAccount()));
        statoAccountColumn.setStyle("-fx-alignment: CENTER;");

        statoAccountColumn.setCellFactory(column -> new TableCell<AuthUser, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                }
                else {
                    setText(item ? "Attivo" : "Disattivato");
                }
            }
        });

        issueAttiveColumn.setCellValueFactory(new PropertyValueFactory<>("issueAttive"));
        tempoMedioColumn.setCellValueFactory(new PropertyValueFactory<>("tempoMedio"));
        issueAttiveColumn.setStyle("-fx-alignment: CENTER;");
        tempoMedioColumn.setStyle("-fx-alignment: CENTER;");

        filteredData = new FilteredList<>(masterData, p -> true);
        utentiTable.setItems(filteredData);

        filtroChoiceBox.getItems().addAll("Tutti", "Attivi", "Non Attivi");
        filtroChoiceBox.setValue("Tutti");

        popolaDashboard();

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
    protected void onStatoIssueButtonClick(){
        Map<String, Integer> dataStates = issueClient.countIssueStates();

        int countToDo = dataStates.getOrDefault("TO_DO", 0);
        int countAssegnati = dataStates.getOrDefault("ASSEGNATO", 0);

        ObservableList<PieChart.Data> issueStates = FXCollections.observableArrayList();

        if(countToDo > 0) issueStates.add(new PieChart.Data("To-Do (" + countToDo + ")", countToDo));
        if(countAssegnati > 0) issueStates.add(new PieChart.Data("Assegnati (" + countAssegnati + ")", countAssegnati));

        bugChart.setData(issueStates);
        bugChart.setTitle("Stato Generale Issue Attive");
    }

    @FXML
    protected void onTipoIssueButtonClick(){
        Map<String, Integer> issuesType = issueClient.countIssueTypes();

        int countBug = issuesType.getOrDefault("BUG", 0);
        int countFeature = issuesType.getOrDefault("FEATURE", 0);
        int countQuestion = issuesType.getOrDefault("QUESTION", 0);
        int countDocumentation = issuesType.getOrDefault("DOCUMENTATION", 0);

        ObservableList<PieChart.Data> issueTypes = FXCollections.observableArrayList();

        if(countBug > 0) issueTypes.add(new PieChart.Data("Bug (" + countBug + ")", countBug));
        if(countFeature > 0) issueTypes.add(new PieChart.Data("Feature (" + countFeature + ")", countFeature));
        if(countDocumentation > 0) issueTypes.add(new PieChart.Data("Documentation (" + countDocumentation + ")", countDocumentation));
        if(countQuestion > 0) issueTypes.add(new PieChart.Data("Question (" + countQuestion + ")", countQuestion));

        bugChart.setData(issueTypes);
        bugChart.setTitle("Stato Generale Issue");
    }

    private void popolaDashboard() {
        onStatoIssueButtonClick();
        onIssueAssegnateButtonClick();
    }

    @FXML
    protected void onTempoButtonClick() {
        ((javafx.scene.chart.CategoryAxis) bugPerUserChart.getXAxis()).getCategories().clear();
        Map<String, Double> dataTimes = authClient.getTimePerUser();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Tempo medio di risoluzione (Ore)");


        dataTimes.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(12)
                .forEach(entry -> {
                    Double tempo = entry.getValue();
                    String email = entry.getKey();
                    double tempoArrotondato = Math.round(tempo * 10.0) / 10.0;
                    String username = email.split("@")[0] + "(" + tempoArrotondato +")";
                    series.getData().add(new XYChart.Data<>(username, tempoArrotondato));

                });

        CategoryAxis xAxis = (CategoryAxis) bugPerUserChart.getXAxis();
        xAxis.setTickLabelRotation(315);

        NumberAxis yAxis = (NumberAxis) bugPerUserChart.getYAxis();
        yAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return String.format(java.util.Locale.US, "%.2f h", object.doubleValue());
            }
            @Override
            public Number fromString(String string){
                return null;
            }
        });

        bugPerUserChart.setTitle("Tempo medio di risoluzione (Ore)");
        bugPerUserChart.setLegendVisible(false);
        bugPerUserChart.getData().clear();
        bugPerUserChart.getData().add(series);
    }

    @FXML
    protected void onIssueAssegnateButtonClick(){
        ((javafx.scene.chart.CategoryAxis) bugPerUserChart.getXAxis()).getCategories().clear();
        Map<String, Integer> issuesPerUser = authClient.getIssuesPerUser();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Bug assegnati per utente");

        issuesPerUser.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(12)
                .forEach(entry -> {
                    String email = entry.getKey();
                    int count = entry.getValue();
                    String username = email.split("@")[0] + "(" + count +")";
                    series.getData().add(new XYChart.Data<>(username, count));

                });

        CategoryAxis xAxis = (CategoryAxis) bugPerUserChart.getXAxis();
        xAxis.setTickLabelRotation(315);

        bugPerUserChart.setTitle("Bug assegnati per utente");
        bugPerUserChart.setLegendVisible(false);
        bugPerUserChart.getData().clear();
        bugPerUserChart.getData().add(series);
        NumberAxis yAxis = (NumberAxis) bugPerUserChart.getYAxis();
        yAxis.setMinorTickVisible(false); // Nasconde le lineette piccole intermedie

        yAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                // Se il numero è intero (resto della divisione per 1 è 0) lo stampa, altrimenti stringa vuota
                if (object.doubleValue() % 1 == 0) {
                    return String.valueOf(object.intValue());
                } else {
                    return "";
                }
            }

            @Override
            public Number fromString(String string) {
                return null; // Non serve per i grafici
            }
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

        Map<String, Integer> issuesPerUser = authClient.getIssuesPerUser();
        Map<String, Double> timeMap = authClient.getTimePerUser();

        for(AuthUser user : users){
            String email = user.getEmail();

            int bugAssegnati = issuesPerUser.getOrDefault(email, 0);
            user.setIssueAttive(bugAssegnati);

            double tempoMedio = timeMap.getOrDefault(email, 0.0);
            double tempoArrotondato = Math.round(tempoMedio*10.0)/10.0;
            user.setTempoMedio(tempoArrotondato);
        }
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
