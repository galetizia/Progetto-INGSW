package org.example.bugboard26frontend;

import client.AuthClient;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class externalUserHomeController {

    @FXML
    private Button logoutButton;

    AuthClient authClient = new AuthClient();

    @FXML
    protected void onElencoBugButtonClick() {
        // Da implementare
    }

    @FXML
    protected void onLogoutButtonClick() {
        // Chiama il metodo di logout del tuo client
        authClient.logout();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login-view.fxml"));
            Parent root = loader.load();

            // Recuperiamo la finestra (Stage) partendo dal bottone di logout
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Errore nell'apertura schermata login");
        }
    }
}