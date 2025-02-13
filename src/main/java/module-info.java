module projectAPI {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    // Gluon Charm Glisten UI
    requires com.gluonhq.charm.glisten;

    // Gluon Attach modules
    requires com.gluonhq.attach.util;
    requires com.gluonhq.attach.display;
    requires com.gluonhq.attach.storage;

    // Export and open controllers
    exports controllers;
    opens controllers to javafx.fxml;
    exports Test to javafx.graphics;

    exports controllers.Events;
    opens controllers.Events to javafx.fxml;
    opens Test to javafx.fxml;
}
