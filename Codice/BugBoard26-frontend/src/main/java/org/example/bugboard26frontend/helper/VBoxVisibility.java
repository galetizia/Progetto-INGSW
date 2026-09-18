package org.example.bugboard26frontend.helper;

import javafx.animation.FadeTransition;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;



public class VBoxVisibility {

    private VBoxVisibility() {}

    public static void visibility(VBox colonnaTrue, VBox colonnaFalse, Runnable onSuccess) {

        boolean isVisible = colonnaTrue.isVisible();
        Stage stage = (Stage) colonnaTrue.getScene().getWindow();

        if (!isVisible) {
            if(colonnaFalse != null) {
                colonnaFalse.setVisible(false);
                colonnaFalse.setManaged(false);
            }

            colonnaTrue.setOpacity(0.0);
            colonnaTrue.setVisible(true);
            colonnaTrue.setManaged(true);

            stage.sizeToScene();
            stage.centerOnScreen();

            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), colonnaTrue);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
            if(onSuccess != null) onSuccess.run();

        } else {
            FadeTransition fadeout = new FadeTransition(Duration.millis(300), colonnaTrue);
            fadeout.setFromValue(1.0);
            fadeout.setToValue(0.0);
            fadeout.setOnFinished(_ -> {
                colonnaTrue.setVisible(false);
                colonnaTrue.setManaged(false);
                stage.sizeToScene();
                stage.centerOnScreen();
            });
            fadeout.play();
        }
    }
}
