package org.example.bugboard26frontend;

import client.AuthClient;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import client.AuthSession;
import javafx.stage.Stage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

// (Il Cervello): È il file Java collegato strettamente alla grafica.
// Qui dentro ci sono i metodi che dicono al programma cosa fare quando l'utente interagisce con la finestra
public class HelloController {
    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    protected void onLoginButtonClick() {
        String email = emailField.getText();
        String password = passwordField.getText();

        AuthClient authClient = new AuthClient();

        if(email.isEmpty() || password.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Attenzione!");
            alert.setHeaderText(null);
            alert.setContentText("Compilare tutti i campi!");
            alert.showAndWait();
            return;
        }
        if(!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Attenzione!");
            alert.setHeaderText(null);
            alert.setContentText("Inserire indirizzo email valido");
            alert.showAndWait();
            return;
        }
        if(!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$")){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Attenzione!");
            alert.setHeaderText(null);
            alert.setContentText("La password deve essere di almeno 8 caratteri, contenere un numero e un carattere speciale (@,#,$,%,^,&,+,=,!)");
            alert.showAndWait();
            return;
        }

        try{
            boolean success = authClient.login(email, password);

            // Controlla il codice di stato: 200 significa che è andato tutto bene
            if(success) {
                Stage stage = (Stage) emailField.getScene().getWindow();

                FXMLLoader loader = new FXMLLoader(getClass().getResource("user-home-view.fxml"));
                Scene scene = new Scene(loader.load(), 880, 480);
                stage.setTitle("Home");
                stage.setScene(scene);
            } else {
                // Se il codice non è 200, le credenziali sono errate
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Errore!");
                alert.setHeaderText(null);
                alert.setContentText("Credenziali non valide");
                alert.showAndWait();
            }
        }catch (Exception e) {
            // Si attiva SOLO se il server è irraggiungibile (es. Spring Boot è spento o non c'è rete)
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore di rete!");
            alert.setHeaderText(null);
            alert.setContentText("Impossibile contattare il server");
            alert.showAndWait();
            e.printStackTrace();
        }




    }
}