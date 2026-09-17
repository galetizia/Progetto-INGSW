package org.example.bugboard26frontend;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.bugboard26frontend.helper.WindowHelper;

public class FrontEnd_BugBoard extends Application {
    @Override
    public void start(Stage stage){
        WindowHelper.tornaAlLogin(stage);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}