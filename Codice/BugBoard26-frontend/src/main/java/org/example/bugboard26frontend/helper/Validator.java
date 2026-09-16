package org.example.bugboard26frontend.helper;

import javafx.scene.control.Alert;

public class Validator {
    private static final MyAlert alert = new MyAlert();

    public static void passwordValidator(String password) {
        if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$")) {
            alert.mostraAlert(Alert.AlertType.WARNING, "Attenzione!",
                    "La password deve essere di almeno 8 caratteri, contenere un numero e un carattere speciale (@,#,$,%,^,&,+,=,!)");
        }
    }
    public static void emailValidator(String email) {
        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            alert.mostraAlert(Alert.AlertType.WARNING, "Attenzione!", "Inserire indirizzo email valido");
        }
    }
}
