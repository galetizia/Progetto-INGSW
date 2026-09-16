package org.example.bugboard26frontend;

import client.AuthClient;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.bugboard26frontend.helper.MyAlert;
import org.example.bugboard26frontend.helper.WindowHelper;

public class LoginController {
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;

    AuthClient authClient = new AuthClient();
    MyAlert alert = new MyAlert();

    @FXML
    protected void onLoginButtonClick() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if(email.isEmpty() || password.isEmpty()) {
            alert.mostraAlert(Alert.AlertType.WARNING, "Attenzione!", "Compilare tutti i campi!");
            return;
        }

        try{
            boolean success = authClient.login(email, password);

            if(success) {
                Stage stage = (Stage) emailField.getScene().getWindow();
                var ruolo = client.AuthSession.getInstance().getUtenteCorrente().getRuolo();

                WindowHelper.apriHome(stage, ruolo);
            } else {
                alert.mostraAlert(Alert.AlertType.ERROR, "Errore!", "Errore imprevisto");
            }
        } catch (IllegalAccessException e) {
            alert.mostraAlert(Alert.AlertType.ERROR, "Accesso negato!", e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            alert.mostraAlert(Alert.AlertType.ERROR, "Errore di Sistema", "Impossibile contattare il server. Riprova più tardi.");
            e.printStackTrace();
        }
    }
}