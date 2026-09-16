package org.example.bugboard26frontend.helper;

import javafx.scene.control.Alert;

public class ControlloPassword {

    public void mostra()
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
}
