module net.phybros.todofx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.prefs;

    opens net.phybros.todofx to javafx.fxml;
    exports net.phybros.todofx;
}
