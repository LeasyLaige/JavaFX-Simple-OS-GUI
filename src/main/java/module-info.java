module com.ambassadors.javafxsimpleosgui {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.ambassadors.javafxsimpleosgui to javafx.fxml;
    exports com.ambassadors.javafxsimpleosgui;
}