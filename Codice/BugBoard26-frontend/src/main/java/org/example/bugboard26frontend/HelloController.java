package org.example.bugboard26frontend;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
// (Il Cervello): È il file Java collegato strettamente alla grafica.
// Qui dentro ci sono i metodi che dicono al programma cosa fare quando l'utente interagisce con la finestra
public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}