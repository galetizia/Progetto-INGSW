package org.example.bugboard26frontend.controller;

import client.AuthClient;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.bugboard26frontend.helper.MyAlert;
import org.example.bugboard26frontend.helper.WindowHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController {
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;

    AuthClient authClient;
    public void setAuthClient(AuthClient authClient){
        this.authClient = authClient;
    }

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @FXML
    protected void onLoginButtonClick() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if(email.isEmpty() || password.isEmpty()) {
            MyAlert.mostraAlert(Alert.AlertType.WARNING, "Attenzione!", "Compilare tutti i campi!");
            return;
        }

        try{
            boolean success = authClient.login(email, password);

            if(success) {
                Stage stage = (Stage) emailField.getScene().getWindow();
                var ruolo = client.AuthSession.getInstance().getUtenteCorrente().getRuolo();

                WindowHelper.apriHome(stage, ruolo);
            } else {
                MyAlert.mostraAlert(Alert.AlertType.ERROR, "Errore!", "Errore imprevisto");
            }
        } catch (IllegalAccessException e) {
            MyAlert.mostraAlert(Alert.AlertType.ERROR, "Accesso negato!", e.getMessage());
            logger.error(e.getMessage(), e);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error(e.getMessage(), e);

        } catch (Exception e) {
            MyAlert.mostraAlert(Alert.AlertType.ERROR, "Errore di Sistema", "Impossibile contattare il server. Riprova più tardi.");
            logger.error(e.getMessage(), e);
        }
    }
}