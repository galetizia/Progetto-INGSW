package org.example.bugboard26frontend;

import javafx.fxml.FXML;
import javafx.scene.control.*;
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

        System.out.println(email);
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

        String jsonBody = """
                {
                    "email": "%s",
                    "password": "%s"
                }
                """.formatted(email,password);

        try{
            // Crea il "motore" che gestirà la connessione
            HttpClient client = HttpClient.newHttpClient();

            // Prepara il pacco: imposta l'indirizzo, specifica che è un JSON e inserisce i dati (POST)
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/api/auth/login")).header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody)).build();

            // Invia il pacco a Spring Boot e aspetta di ricevere la risposta sotto forma di testo
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Controlla il codice di stato: 200 significa che è andato tutto bene
            if(response.statusCode() == 200) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Login");
                alert.setHeaderText(null);
                alert.setContentText("Benvenuto!"+ response.body());
                alert.showAndWait();
            } else {
                // Se il codice non è 200, le credenziali sono errate
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Errore!");
                alert.setHeaderText(null);
                alert.setContentText("Errore"+response.body());
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