package org.example.bugboard26frontend.helper;

import javafx.animation.FadeTransition;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;


/**
 * Classe Helper per gestire le animazioni di transizione (fade in / fade out) e la visibilità dei pannelli.
 * Si occupa non solo dell'effetto grafico, ma anche del corretto ridimensionamento e del ri-centramento della finestra.
 */
public class VBoxVisibility {

    private VBoxVisibility() {}

    /**
     * Alterna la visibilità dei pannelli con un morbido effetto di dissolvenza.
     * Quando il pannello viene mostrato, nasconde automaticamente un eventuale pannello secondario in conflitto
     * e ricalcola gli spazi occupati per evitare buchi vuoti nell'interfaccia.
     *
     * @param colonnaTrue  Il pannello target di cui si desidera invertire lo stato di visibilità.
     * @param colonnaFalse Un pannello secondario e opzionale da nascondere istantaneamente quando si mostra il principale.
     * @param onSuccess    Azione di callback eseguita contestualmente all'avvio dell'animazione di comparsa.
     */
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
