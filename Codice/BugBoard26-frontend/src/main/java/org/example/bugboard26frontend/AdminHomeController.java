package org.example.bugboard26frontend;

import client.AuthClient;
import client.IssueClient;
import enums.Ruolo;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Issue;
import org.example.bugboard26frontend.helper.CambioPassword;
import org.example.bugboard26frontend.helper.FiltroEOrdinaHelper;
import org.example.bugboard26frontend.helper.IssueTableHelper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

public class AdminHomeController {
    @FXML
    private MenuItem logoutButton;

    @FXML
    private Button elencoButton;
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

    AuthClient authClient = new AuthClient();
    IssueClient issueClient = new IssueClient();
    private ObservableList<Issue> masterData = FXCollections.observableArrayList();

    @FXML
    public void initialize()
    {

        IssueTableHelper.configuraTabella(issueTable, Ruolo.ADMIN, false);

        IssueTableHelper.configuraTabella(archiviatiTable, Ruolo.ADMIN, true);

        FiltroEOrdinaHelper.configuraFiltroEOrdine(issueTable, masterData, filtroChoiceBox, ordinaChoiceBox, Ruolo.ADMIN);


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
        CambioPassword cambioPassword = new CambioPassword(authClient);
        cambioPassword.mostra();
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
