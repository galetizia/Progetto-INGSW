module org.example.bugboard26frontend {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.net.http;


    opens org.example.bugboard26frontend to javafx.fxml;
    exports org.example.bugboard26frontend;
}