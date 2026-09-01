module org.example.bugboard26frontend {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.bugboard26frontend to javafx.fxml;
    exports org.example.bugboard26frontend;
}