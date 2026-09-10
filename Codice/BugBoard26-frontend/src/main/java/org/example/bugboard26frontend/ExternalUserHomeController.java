package org.example.bugboard26frontend;

import client.AuthClient;
import client.IssueClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Issue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

public class ExternalUserHomeController {

    @FXML
    private MenuItem logoutButton;

    AuthClient authClient = new AuthClient();
    IssueClient issueClient = new IssueClient();
    private ObservableList<Issue> masterData = FXCollections.observableArrayList();
    private FilteredList<Issue> filteredData;
    private SortedList<Issue> sortedData;

    @FXML
    private TableView<Issue> bugTable;
    @FXML
    private Button visualizzaAllegatoButton;
    @FXML
    private TableColumn<Issue, Integer> idColumn;
    @FXML
    private TableColumn<Issue, String> titoloColumn;
    @FXML
    private TableColumn<Issue, String> statoColumn;
    @FXML
    private TableColumn<Issue,String> tipoColumn;
    @FXML
    private TableColumn<Issue, String> prioritaColumn;
    @FXML
    private TableColumn<Issue, LocalDateTime> dataColumn;
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
        // Setup colonne Issue Attive
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titoloColumn.setCellValueFactory(new PropertyValueFactory<>("titolo"));
        statoColumn.setCellValueFactory(new PropertyValueFactory<>("stato"));
        tipoColumn.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        prioritaColumn.setCellValueFactory(new PropertyValueFactory<>("priorita"));
        dataColumn.setCellValueFactory(new PropertyValueFactory<>("data"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        dataColumn.setCellFactory(column -> new TableCell<Issue, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime date, boolean empty) {
                super.updateItem(date, empty);

                if (empty || date == null) {
                    setText(null); // Se la riga è vuota, non scrivere nulla
                } else {
                    setText(formatter.format(date));
                }
            }
        });


        bugTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if(newValue != null) {
                descriptionArea.setText(newValue.getDescrizione());
                visualizzaAllegatoButton.setDisable(newValue.getAllegato()==null);
            }
            else {
                descriptionArea.setText("");
                visualizzaAllegatoButton.setDisable(true);
            }
        });

        filteredData = new FilteredList<>(masterData, p -> "BUG".equalsIgnoreCase(p.getTipo().name()));
        sortedData = new SortedList<>(filteredData);
        bugTable.setItems(sortedData);

        filtroChoiceBox.getItems().addAll("Tutte", "To-do", "Bug", "Feature", "Documentation", "Question");
        filtroChoiceBox.setValue("Tutte");

        ordinaChoiceBox.getItems().addAll("Nessun ordine", "Priorità Alta", "Più recenti");
        ordinaChoiceBox.setValue("Nessun ordine");

        filtroChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newValue) -> {
            applicaFiltroEOrdine();
        });

        // Ascoltatore per gli ordini
        ordinaChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newValue) -> {
            applicaFiltroEOrdine();
        });
    }

    private void applicaFiltroEOrdine(){
        if(filteredData == null || sortedData == null) return;

        String filtro = filtroChoiceBox.getValue();
        String ordina = ordinaChoiceBox.getValue();

        filteredData.setPredicate(issue -> {
            if ("To-do".equals(filtro)) return "TO_DO".equalsIgnoreCase(issue.getStato());
            if ("Bug".equals(filtro)) return "BUG".equalsIgnoreCase(issue.getTipo().name());
            if ("Feature".equals(filtro)) return "FEATURE".equalsIgnoreCase(issue.getTipo().name());
            if ("Documentation".equals(filtro)) return "DOCUMENTATION".equalsIgnoreCase(issue.getTipo().name());
            if ("Question".equals(filtro)) return "QUESTION".equalsIgnoreCase(issue.getTipo().name());
            return true;
        });

        if("Priorità Alta".equals(ordina)){
            List<String> ordine = List.of("ALTA", "MEDIA", "BASSA", "NO");
            sortedData.setComparator(Comparator.comparingInt(issue -> {
                String priorita = String.valueOf(issue.getPriorita()).toUpperCase();
                int posizione = ordine.indexOf(priorita);
                return posizione == -1 ? Integer.MAX_VALUE : posizione;
            }));
        } else if("Più recenti".equals(ordina)){
            sortedData.setComparator(
                    Comparator.comparing(Issue::getData, Comparator.nullsLast(Comparator.naturalOrder()))
                            .reversed());
        } else{
            sortedData.setComparator(null);
        }
    }

    @FXML
    protected void onElencoBugButtonClick() {
        boolean isVisible = colonnaSinistra.isVisible();

        if (!isVisible) {
            loadOnTable();
            colonnaSinistra.setVisible(true);
            colonnaSinistra.setManaged(true);
        } else {
            colonnaSinistra.setVisible(false);
            colonnaSinistra.setManaged(false);
        }

        Stage stage = (Stage) colonnaSinistra.getScene().getWindow();
        javafx.application.Platform.runLater(() -> {
            stage.sizeToScene();
            stage.centerOnScreen();
        });
    }
    private void loadOnTable() {
        List<Issue> issues = issueClient.elencoIssue();
        masterData.setAll(issues);
    }

    @FXML
    protected void onVisualizzaAllegatoButtonClick() {
        Issue issue = bugTable.getSelectionModel().getSelectedItem();
        if(issue != null && issue.getAllegato()!=null) {

            try {
                byte[] data = issue.getAllegato().getContenuto();
                ByteArrayInputStream bais = new ByteArrayInputStream(data);
                Image image = new Image(bais);

                ImageView imageView = new ImageView(image);
                imageView.setPreserveRatio(true);
                imageView.setFitHeight(1000);
                imageView.setFitWidth(800);

                StackPane layout = new StackPane(imageView);
                layout.setStyle("-fx-background-color: #0b0914; -fx-padding: 20;");

                Stage imgStage = new Stage();
                imgStage.setTitle("Allegato: "+issue.getAllegato().getNome());
                imgStage.setScene(new Scene(layout));
                //imgStage.initModality(Modality.APPLICATION_MODAL);

                Stage mainWindow = (Stage) visualizzaAllegatoButton.getScene().getWindow();
                imgStage.initOwner(mainWindow);
                imgStage.showAndWait();
            } catch (Exception e){
                System.out.println("Errore nell'apertura allegato" + e.getMessage());
            }
        }

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
    protected void onCambioPasswordButtonClick() {

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