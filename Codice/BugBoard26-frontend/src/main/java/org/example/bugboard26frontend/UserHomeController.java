package org.example.bugboard26frontend;

import client.AuthClient;
import client.IssueClient;
import enums.Ruolo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import client.AuthSession;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Issue;
import org.example.bugboard26frontend.helper.CambioPassword;
import org.example.bugboard26frontend.helper.FiltroEOrdinaHelper;
import org.example.bugboard26frontend.helper.IssueTableHelper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

public class UserHomeController {
    @FXML
    private MenuItem logoutButton;

    AuthClient authClient = new AuthClient();
    IssueClient issueClient = new IssueClient();
    private ObservableList<Issue> masterData = FXCollections.observableArrayList();

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
        IssueTableHelper.configuraTabella(issueTable, Ruolo.INTERNAL_USER, false);

        IssueTableHelper.configuraTabella(archiviatiTable, Ruolo.INTERNAL_USER, true);

        FiltroEOrdinaHelper.configuraFiltroEOrdine(issueTable, masterData, filtroChoiceBox, ordinaChoiceBox, Ruolo.INTERNAL_USER);

        issueTable.getSelectionModel().selectedItemProperty().addListener((_, _, newValue) -> {
            if(newValue != null) {
                descriptionArea.setText(newValue.getDescrizione());
                visualizzaAllegatoButton.setDisable(newValue.getAllegato() == null);

                boolean isToDo = "TO_DO".equalsIgnoreCase(newValue.getStato());
                boolean isMiaInLavorazione = "ASSEGNATO".equalsIgnoreCase(newValue.getStato())
                        && newValue.getAssignee() != null
                        && newValue.getAssignee().getId() == AuthSession.getInstance().getUtenteCorrente().getId();

                if (isToDo) {
                    prendiInCaricoButton.setText("Prendi in carico");
                    prendiInCaricoButton.setDisable(false);
                    if (rilasciaIssueButton != null) rilasciaIssueButton.setDisable(true);

                } else if (isMiaInLavorazione) {
                    prendiInCaricoButton.setText("Segna come Risolto");
                    prendiInCaricoButton.setDisable(false);
                    if (rilasciaIssueButton != null) rilasciaIssueButton.setDisable(false);

                } else {
                    prendiInCaricoButton.setText("Prendi in carico");
                    prendiInCaricoButton.setDisable(true);
                    if (rilasciaIssueButton != null) rilasciaIssueButton.setDisable(true);
                }
            }
            else {
                descriptionArea.setText("");
                visualizzaAllegatoButton.setDisable(true);
                prendiInCaricoButton.setText("Prendi in carico");
                prendiInCaricoButton.setDisable(true);
                if (rilasciaIssueButton != null) rilasciaIssueButton.setDisable(true);
            }
        });
    }

    @FXML
    protected void onElencoIssueButtonClick() {
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
            Stage mainWindow = (Stage) issueTable.getScene().getWindow();
            dialogStage.initOwner(mainWindow);

            dialogStage.showAndWait();
            loadOnTable();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Errore nell'apertura finestra segnalazione");
        }
    }

    @FXML
    protected void onBugArchiviatiButtonClick() {
        boolean isVisible = colonnaDestra.isVisible();

        if (!isVisible) {
            loadArchiviatiOnTable();
            colonnaDestra.setVisible(true);
            colonnaDestra.setManaged(true);
        } else {
            colonnaDestra.setVisible(false);
            colonnaDestra.setManaged(false);
        }

        Stage stage = (Stage) colonnaDestra.getScene().getWindow();
        javafx.application.Platform.runLater(() -> {
            stage.sizeToScene();
            stage.centerOnScreen();
        });
    }

    private void loadOnTable() {
        List<Issue> issues = issueClient.getIssueAttive();
        masterData.setAll(issues);
    }

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

                Stage mainWindow = (Stage) visualizzaAllegatoButton.getScene().getWindow();
                imgStage.initOwner(mainWindow);
                imgStage.showAndWait();
            } catch (Exception e){
                System.out.println("Errore nell'apertura allegato" + e.getMessage());
            }
        }
    }

    @FXML
    protected void prendiInCaricoButtonClick(){
        Issue issueSelezionata = issueTable.getSelectionModel().getSelectedItem();

        if (issueSelezionata != null && AuthSession.getInstance().isLoggedIn()) {

            // L'utente sta prendendo in carico la issue
            if ("TO_DO".equalsIgnoreCase(issueSelezionata.getStato())) {
                boolean success = issueClient.prendiInCarico(issueSelezionata.getId());
                if (success) {
                    loadOnTable();
                    mostraAlert(Alert.AlertType.INFORMATION, "Successo", "Hai preso in carico la issue #" + issueSelezionata.getId());
                } else {
                    mostraAlert(Alert.AlertType.ERROR, "Errore", "Impossibile prendere in carico la issue.");
                }
            }
            // L'utente sta risolvendo la sua issue
            else if ("ASSEGNATO".equalsIgnoreCase(issueSelezionata.getStato())) {
                boolean success = issueClient.risolviIssue(issueSelezionata.getId());
                if (success) {
                    loadOnTable();
                    mostraAlert(Alert.AlertType.INFORMATION, "Successo", "Issue #" + issueSelezionata.getId() + " segnata come Risolta!");
                } else {
                    mostraAlert(Alert.AlertType.ERROR, "Errore", "Impossibile risolvere la issue.");
                }
            }
            javafx.application.Platform.runLater(() -> issueTable.requestFocus());
        }
    }

    @FXML
    protected void rilasciaIssueButtonClick() {
        Issue issueSelezionata = issueTable.getSelectionModel().getSelectedItem();

        if (issueSelezionata != null) {
            Alert conferma = new Alert(Alert.AlertType.CONFIRMATION);
            conferma.setTitle("Conferma");
            conferma.setHeaderText("Rilascio Issue #" + issueSelezionata.getId());
            conferma.setContentText("Sei sicuro di voler rimettere questa issue in stato TO_DO?");

            conferma.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {

                    boolean success = issueClient.rilasciaIssue(issueSelezionata.getId());
                    if (success) {
                        loadOnTable();
                        mostraAlert(Alert.AlertType.INFORMATION, "Successo", "Hai rilasciato la issue.");
                    } else {
                        mostraAlert(Alert.AlertType.ERROR, "Errore", "Si è verificato un problema di comunicazione col server.");
                    }
                }
                javafx.application.Platform.runLater(() -> issueTable.requestFocus());
            });
        }
    }

    // Metodo di supporto per gli alert
    private void mostraAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    protected void onCambioPasswordButtonClick(){
        CambioPassword cambioPassword = new CambioPassword(authClient);
        cambioPassword.mostra();
    }

}
