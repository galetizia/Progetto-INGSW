package org.example.bugboard26frontend;

import client.AuthClient;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class CreazioneUtenteController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private MenuButton ruoloMenuButton;
    @FXML private Button confermaButton;

    private String ruoloScelto = "";

    AuthClient authClient = new AuthClient();

    @FXML
    protected void onAdminSelezionato() {
        ruoloScelto = "ADMIN";
        ruoloMenuButton.setText("Ruolo: ADMIN");
    }

    @FXML
    protected void onInternalUserSelezionato() {
        ruoloScelto = "INTERNAL_USER";
        ruoloMenuButton.setText("Ruolo: INTERNAL_USER");
    }

    @FXML
    protected void onExternalUserSelezionato() {
        ruoloScelto = "EXTERNAL_USER";
        ruoloMenuButton.setText("Ruolo: EXTERNAL_USER");
    }

    @FXML
    protected void onConfermaButtonClick() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isBlank() || password.isBlank() || ruoloScelto.isBlank()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Dati mancanti");
            alert.setHeaderText(null);
            alert.setContentText("Inserire Email, Password e scegliere un Ruolo.");
            alert.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma operazione");
        alert.setHeaderText("Stai per creare un nuovo utente con ruolo " + ruoloScelto);
        alert.setContentText("Procedere?");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                System.out.println("Salvataggio utente in corso...");

                // Effettuiamo la chiamata tramite AuthClient
                boolean success = authClient.registerUser(email, password, ruoloScelto);

                if (success) {
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Utente creato");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText("Utente creato con successo!");
                    successAlert.showAndWait();
                    chiudiFinestra();
                } else {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Errore!");
                    errorAlert.setHeaderText(null);
                    errorAlert.setContentText("Impossibile creare l'utente. Controlla che l'email non sia già in uso.");
                    errorAlert.showAndWait();
                }
            } else {
                System.out.println("Operazione annullata");
            }
        });
    }

    @FXML
    protected void onAnnullaButtonClick() {
        chiudiFinestra();
    }

    private void chiudiFinestra() {
        Stage stage = (Stage) confermaButton.getScene().getWindow();
        stage.close();
    }
}