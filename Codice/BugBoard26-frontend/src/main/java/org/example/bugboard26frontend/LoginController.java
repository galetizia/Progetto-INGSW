package org.example.bugboard26frontend;

import client.AuthClient;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

// (Il Cervello): È il file Java collegato strettamente alla grafica.
// Qui dentro ci sono i metodi che dicono al programma cosa fare quando l'utente interagisce con la finestra
public class LoginController {
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

            if(success) {
                Stage stage = (Stage) emailField.getScene().getWindow();

                var ruolo = client.AuthSession.getUtenteCorrente().getRuolo();
                String viewToLoad = "";

                switch (ruolo) {
                    case ADMIN:
                        viewToLoad = "admin-home-view.fxml";
                        break;
                    case EXTERNAL_USER:
                        viewToLoad = "externalUser-home-view.fxml";
                        break;
                    case INTERNAL_USER:
                        viewToLoad = "user-home-view.fxml";
                        break;
                    default:
                        viewToLoad = "user-home-view.fxml"; // Fallback di sicurezza
                        break;
                }

                FXMLLoader loader = new FXMLLoader(getClass().getResource(viewToLoad));
                Scene scene = new Scene(loader.load());
                stage.setTitle("BugBoard - " + ruolo.name());
                stage.setScene(scene);

                stage.sizeToScene();
                stage.centerOnScreen();
            } else {
                // Se il codice non è 200, le credenziali sono errate
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Errore!");
                alert.setHeaderText(null);
                alert.setContentText("Errore imprevisto");
                alert.showAndWait();
            }
        } catch (IllegalArgumentException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Accesso negato!");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}