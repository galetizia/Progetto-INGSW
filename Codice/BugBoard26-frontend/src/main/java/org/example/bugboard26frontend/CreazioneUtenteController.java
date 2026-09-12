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

        // 1. Controllo campi vuoti
        if(email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            mostraAlert(Alert.AlertType.WARNING, "Attenzione!", "Compilare tutti i campi (Email e Password).");
            return; // Blocca l'esecuzione
        }

        // 2. Controllo validità Email
        if(!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")){
            mostraAlert(Alert.AlertType.WARNING, "Attenzione!", "Inserire un indirizzo email valido (@ e .com/.it).");
            return;
        }

        // 3. Controllo validità Password
        if(!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$")){
            mostraAlert(Alert.AlertType.WARNING, "Attenzione!", "La password deve essere di almeno 8 caratteri, contenere un numero, una lettera maiuscola, una minuscola e un carattere speciale (@,#,$,%,^,&,+,=,!).");
            return;
        }

        // 4. Controllo selezione Ruolo
        if(ruoloScelto.isEmpty()) {
            mostraAlert(Alert.AlertType.WARNING, "Attenzione!", "Selezionare un ruolo dal menu a tendina.");
            return;
        }

        // Se tutti i controlli vengono superati, procedi con la creazione!
        boolean success = authClient.registerUser(email, password, ruoloScelto);

        if (success) {
            // Chiudi il pop-up se la creazione è andata a buon fine
            Stage stage = (Stage) confermaButton.getScene().getWindow();
            stage.close();
        } else {
            mostraAlert(Alert.AlertType.ERROR, "Errore!", "Impossibile creare l'utente. Controlla che l'email non sia già in uso.");
        }
    }

    // Metodo di supporto per creare gli Alert senza ripetere sempre le stesse righe
    private void mostraAlert(Alert.AlertType tipo, String titolo, String messaggio) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
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