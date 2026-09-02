package org.example.bugboard26frontend;

import client.AuthClient;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import client.AuthSession;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class UserHomeController {
    @FXML
    private Button logoutButton;
    AuthClient authClient = new AuthClient();

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
            Stage mainWindow = (Stage) logoutButton.getScene().getWindow();
            dialogStage.initOwner(mainWindow);

            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Errore nell'apertura finestra segnalazione");
        }
    }

    @FXML
    protected void onVisualizzaElencoIssueButtonClick(){

    }

    @FXML
    protected void onLogoutButtonClick() {
        authClient.logout();

        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login-view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e){
            e.printStackTrace();
            System.out.println("Errore nell'apertura schermata login");
        }
    }
}
