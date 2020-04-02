module project.periode {
    requires javafx.fxml;
    requires javafx.controls;
    
    opens framework;
    opens games.reversi;
    opens games.ABC;
}