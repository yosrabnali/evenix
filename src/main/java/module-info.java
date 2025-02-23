module projectAPI {
    requires javafx.fxml;
    requires java.sql;

    // Gluon Charm Glisten UI
    requires com.gluonhq.charm.glisten;

    // Gluon Attach modules
    requires com.gluonhq.attach.util;
    requires com.gluonhq.attach.display;
    requires com.gluonhq.attach.storage;
    requires javafx.web;
    requires jdk.jsobject;
    requires java.net.http;
    requires java.mail;

    // Export et ouverture des packages pour JavaFX
    exports controllers;
    opens controllers to javafx.fxml;

    exports controllers.Events;
    opens controllers.Events to javafx.fxml;




    exports controllers.Locations;  // ✅ Exporte le package
    opens controllers.Locations to javafx.fxml;  // ✅ Ouvre le package à JavaFX

    exports Test;
    opens Test to javafx.fxml;
}
