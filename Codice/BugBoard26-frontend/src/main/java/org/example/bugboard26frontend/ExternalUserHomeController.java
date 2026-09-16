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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Issue;
import org.example.bugboard26frontend.helper.CambioPassword;
import org.example.bugboard26frontend.helper.FiltroEOrdinaHelper;
import org.example.bugboard26frontend.helper.IssueTableHelper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

public class ExternalUserHomeController {

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

        // Rimuove dalla lista tutte le issue con stato "ARCHIVIATO"
        issues.removeIf(issue -> "ARCHIVIATO".equalsIgnoreCase(issue.getStato()));

        masterData.setAll(issues);
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
    protected void onCambioPasswordButtonClick() {
        CambioPassword cambioPassword = new CambioPassword(authClient);
        cambioPassword.mostra();
    }

}