module org.example.etlap {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens org.example.etlap to javafx.fxml;
    exports org.example.etlap;
}