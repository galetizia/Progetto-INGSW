package org.example.bugboard26frontend;

import client.AuthClient;
import client.AuthSession;
import client.IssueClient;
import javafx.animation.FadeTransition;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Issue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

public class AdminHomeController {
    @FXML
    private MenuItem logoutButton;

    @FXML
    private Button elencoButton;
    AuthClient authClient = new AuthClient();
    IssueClient issueClient = new IssueClient();
    private ObservableList<Issue> masterData = FXCollections.observableArrayList();
    private FilteredList<Issue> filteredData;
    private SortedList<Issue> sortedData;
    @FXML
    private Button segnalaComeDuplicatoButton;
    @FXML
    private TableView<Issue> issueTable;
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
    private TableView<Issue> archiviatiTable;
    @FXML
    private TableColumn<Issue, Integer> idArchiviatiColumn;
    @FXML
    private TableColumn<Issue, String> titoloArchiviatiColumn;
    @FXML
    private TableColumn<Issue, String> prioritaArchiviatiColumn;
    @FXML
    private TableColumn<Issue, String> tipoArchiviatiColumn;
    @FXML
    private TableColumn<Issue, LocalDateTime> dataArchiviatiColumn;
    @FXML
    private TableColumn<Issue, String> statoArchiviatiColumn;

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

    @FXML
    public void initialize()
    {
        // Setup colonne Issue Attive
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titoloColumn.setCellValueFactory(new PropertyValueFactory<>("titolo"));
        statoColumn.setCellValueFactory(new PropertyValueFactory<>("stato"));
        tipoColumn.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        prioritaColumn.setCellValueFactory(new PropertyValueFactory<>("priorita"));

        prioritaColumn.setCellFactory(column -> new TableCell<Issue, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    if(item.equalsIgnoreCase("no"))
                        setText("-");
                    else
                        setText(item);
                }
                setStyle("-fx-alignment: CENTER;");
            }
        });

        dataColumn.setCellValueFactory(new PropertyValueFactory<>("data"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        statoColumn.setCellFactory(column -> new TableCell<Issue, String>() {
            @Override
            protected void updateItem(String stato, boolean empty) {
                super.updateItem(stato, empty);

                if (empty || stato == null) {
                    setText(null);
                } else {
                    if ("TO_DO".equalsIgnoreCase(stato)) {
                        setText("🟢 TO-DO");
                    } else if ("ASSEGNATO".equalsIgnoreCase(stato)) {
                        setText("🔒 ASSEGNATO");
                    } else {
                        setText(stato);
                    }
                }
            }
        });

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


        // Setup colonne Bug Archiviati (assicurati di avere dataRisoluzione nell'Entity)
        idArchiviatiColumn.setCellValueFactory(new PropertyValueFactory<>("Id"));
        titoloArchiviatiColumn.setCellValueFactory(new PropertyValueFactory<>("Titolo"));
        prioritaArchiviatiColumn.setCellValueFactory(new PropertyValueFactory<>("Priorita"));
        prioritaArchiviatiColumn.setCellFactory(column -> new TableCell<Issue, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    if(item.equalsIgnoreCase("no"))
                        setText("-");
                    else
                        setText(item);
                }
                setStyle("-fx-alignment: CENTER;");
            }
        });
        tipoArchiviatiColumn.setCellValueFactory(new PropertyValueFactory<>("Tipo"));
        dataArchiviatiColumn.setCellValueFactory(new PropertyValueFactory<>("dataRisoluzione"));

        dataArchiviatiColumn.setCellFactory(column -> new TableCell<Issue, LocalDateTime>(){
            @Override
            protected void updateItem(LocalDateTime date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                }  else {
                    Issue issue = getTableRow().getItem();
                    if (issue != null && issue.getStato() != null) {
                        if (issue.getStato().toString().equalsIgnoreCase("RISOLTO"))
                            setText(formatter.format(date));
                        else
                            setText("-");
                    }
                }
                setStyle("-fx-alignment: CENTER;");
            }
        });

        statoArchiviatiColumn.setCellValueFactory(new PropertyValueFactory<>("Stato"));
        statoArchiviatiColumn.setCellFactory(column -> new TableCell<Issue, String>() {
            @Override
            protected void updateItem(String stato, boolean empty) {
                super.updateItem(stato, empty);
                if (empty || stato == null) {
                    setText(null);
                } else {
                    if ("RISOLTO".equalsIgnoreCase(stato)) {
                        setText("✅ RISOLTO");
                    } else if ("ARCHIVIATO".equalsIgnoreCase(stato)) {
                        setText("📦 ARCHIVIATO");
                    } else {
                        setText(stato);
                    }
                }
            }
        });

        issueTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newValue) -> {
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

        filteredData = new FilteredList<>(masterData, p -> true);
        sortedData = new SortedList<>(filteredData);

        issueTable.setItems(sortedData);

        filtroChoiceBox.getItems().addAll("Tutte", "To-do", "Bug", "Feature", "Documentation", "Question", "Le mie issue");
        filtroChoiceBox.setValue("Tutte");

        ordinaChoiceBox.getItems().addAll("Nessun ordine", "Priorità Alta", "Priorità Bassa", "Più recenti");
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
            if ("Le mie issue".equals(filtro)) return "ASSEGNATO".equalsIgnoreCase(issue.getStato());
            return true;
        });

        if("Priorità Alta".equals(ordina)){
            List<String> ordine = List.of("ALTA", "MEDIA", "BASSA", "NO");
            sortedData.setComparator(Comparator.comparingInt(issue -> {
                String priorita = String.valueOf(issue.getPriorita()).toUpperCase();
                int posizione = ordine.indexOf(priorita);
                return posizione == -1 ? Integer.MAX_VALUE : posizione;
            }));
        } else if ("Priorità Bassa".equals(ordina)) {
            List<String> ordine = List.of("BASSA", "MEDIA", "ALTA", "NO");
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

    public void onElencoIssueButtonClick(){
        boolean isVisible = colonnaSinistra.isVisible();
        Stage stage = (Stage) colonnaSinistra.getScene().getWindow();

        if (!isVisible) {
            loadOnTable();
            colonnaDestra.setVisible(false);
            colonnaDestra.setManaged(false);

            colonnaSinistra.setOpacity(0.0);
            colonnaSinistra.setVisible(true);
            colonnaSinistra.setManaged(true);

            stage.sizeToScene();
            stage.centerOnScreen();

            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), colonnaSinistra);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        } else {
            FadeTransition fadeout = new FadeTransition(Duration.millis(300), colonnaSinistra);
            fadeout.setFromValue(1.0);
            fadeout.setToValue(0.0);
            fadeout.setOnFinished(event -> {
                colonnaSinistra.setVisible(false);
                colonnaSinistra.setManaged(false);
                stage.sizeToScene();
                stage.centerOnScreen();
            });
            fadeout.play();
        }
    }

    public void onSegnalaIssueButtonClick(){
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
            loadOnTable();

            javafx.application.Platform.runLater(() -> issueTable.requestFocus());

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Errore nell'apertura finestra segnalazione");
        }
    }
    @FXML
    protected void onArchivioBugButtonClick() {
        boolean isVisible = colonnaDestra.isVisible();
        Stage stage = (Stage) colonnaDestra.getScene().getWindow();

        if (!isVisible) {
            loadArchiviatiOnTable();
            colonnaSinistra.setVisible(false);
            colonnaSinistra.setManaged(false);

            colonnaDestra.setVisible(true);
            colonnaDestra.setManaged(true);
            stage.sizeToScene();
            stage.centerOnScreen();
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), colonnaDestra);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        } else {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), colonnaDestra);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(event -> {
                colonnaDestra.setVisible(false);
                colonnaDestra.setManaged(false);
                stage.sizeToScene();
                stage.centerOnScreen();
            });
            fadeOut.play();
        }
    }

    //Carica la tabella delle issue attive (TO DO oppure ASSEGNATE)
    private void loadOnTable() {
        List<Issue> issues = issueClient.getIssueAttive();
        masterData.setAll(issues);
    }

    //Carica la tabella delle issue archiviate (ARCHIVIATE e RISOLTE)
    private void loadArchiviatiOnTable() {
        List<Issue> issues = issueClient.getIssueArchiviate();
        ObservableList<Issue> observableList = FXCollections.observableArrayList(issues);
        archiviatiTable.setItems(observableList);
    }

    @FXML
    protected void onLogoutButtonClick() {
        authClient.logout();

        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login-view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) logoutButton.getParentPopup().getOwnerWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("BugBoard - Login");
            stage.show();
            stage.sizeToScene();
            stage.centerOnScreen();

        } catch (IOException e){
            e.printStackTrace();
            System.out.println("Errore nell'apertura schermata login");
        }
    }

    @FXML
    protected void onVisualizzaAllegatoButtonClick() {
        Issue issue = issueTable.getSelectionModel().getSelectedItem();
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
    public void handleArchiviaIssue() {
        Issue issueSelezionata = issueTable.getSelectionModel().getSelectedItem();

        if (issueSelezionata != null) {
            Alert conferma = new Alert(Alert.AlertType.CONFIRMATION);
            conferma.setTitle("Conferma Archiviazione");
            conferma.setHeaderText("Archiviazione Issue #" + issueSelezionata.getId());
            conferma.setContentText("Sei sicuro di voler archiviare: '" + issueSelezionata.getTitolo() + "'?");

            conferma.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    boolean successo = issueClient.archiviaIssue(issueSelezionata.getId());

                    if (successo) {
                        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                        successAlert.setTitle("Successo");
                        successAlert.setHeaderText(null);
                        successAlert.setContentText("Issue archiviata con successo!");
                        successAlert.showAndWait();

                        // Ricarichiamo le tabelle: sparirà da sinistra e andrà a destra!
                        loadOnTable();
                        if (colonnaDestra.isVisible()) {
                            loadArchiviatiOnTable();
                        }
                        archiviaIssueButton.setDisable(true); // Resettiamo il bottone
                    } else {
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setTitle("Errore");
                        errorAlert.setHeaderText(null);
                        errorAlert.setContentText("Si è verificato un problema di comunicazione col server.");
                        errorAlert.showAndWait();
                    }
                }

                javafx.application.Platform.runLater(() -> issueTable.requestFocus());
            });
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

    @FXML
    protected void onSegnalaComeDuplicatoButtonClick(){
        Issue issue = issueTable.getSelectionModel().getSelectedItem();
        if(issue != null){
            Alert confirmDelete = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDelete.setTitle("Conferma eliminazione");
            confirmDelete.setHeaderText("Segnalazione Issue #" + issue.getId());
            confirmDelete.setContentText("Sei sicuro di voler segnalare la issue come duplicata?\n\nATTENZIONE: Questa operazione eliminerà definitivamente la issue e non potrà essere recuperata.");
            confirmDelete.showAndWait().ifPresent(response -> {
                if(response == ButtonType.OK){

                    boolean success = issueClient.eliminaIssue(issue.getId());
                    if(success){
                        masterData.remove(issue);

                        // Pop-up di successo
                        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                        successAlert.setTitle("Successo");
                        successAlert.setHeaderText(null);
                        successAlert.setContentText("Issue eliminata con successo.");
                        successAlert.showAndWait();

                        // Disabilitiamo i bottoni visto che la issue non c'è più
                        segnalaComeDuplicatoButton.setDisable(true);
                        archiviaIssueButton.setDisable(true);
                        visualizzaAllegatoButton.setDisable(true);

                    } else {
                        Alert error = new Alert(Alert.AlertType.ERROR);
                        error.setTitle("Errore");
                        error.setHeaderText(null);
                        error.setContentText("Impossibile eliminare l'issue. Verifica la connessione al server.");
                        error.showAndWait();
                    }
                }

                javafx.application.Platform.runLater(() -> issueTable.requestFocus());
            });
        }
    }

    @FXML
    protected void onGestioneUtentiButtonClick(){
        try{
            Stage stage = (Stage) elencoButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("gestione-utenti-view.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setTitle("Gestione utenti");
            stage.setScene(scene);
            stage.sizeToScene();
            stage.centerOnScreen();
        } catch(Exception e){
            e.printStackTrace();
        }



    }

}
