module org.example.bugboard26frontend {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.net.http;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;


    opens org.example.bugboard26frontend to javafx.fxml;
    opens model to com.fasterxml.jackson.databind;

    exports org.example.bugboard26frontend;
    exports model;
}