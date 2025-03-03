module projectAPI {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.web;
    requires mysql.connector.j;
    requires java.desktop;  // ✅ Pour la gestion des fichiers avec Desktop.getDesktop().open(file)

    // Gluon Charm Glisten UI
    requires com.gluonhq.charm.glisten;

    // Gluon Attach modules
    requires com.gluonhq.attach.util;
    requires com.gluonhq.attach.display;
    requires com.gluonhq.attach.storage;
    requires jdk.jsobject;
    requires java.net.http;

    requires java.mail;

    requires java.management;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires org.apache.xmlbeans;
    requires org.apache.commons.compress;
    requires itextpdf;
    requires com.gluonhq.maps;  // Add this line to read the Gluon Maps module

    // Export and open controllers
    // Export et ouverture des packages pour JavaFX
    exports controllers;
    opens controllers to javafx.fxml;

    exports controllers.Events;
    opens controllers.Events to javafx.fxml;

    // Export and open controllers.Reclamations
    exports controllers.Reclamations to javafx.fxml;
    opens controllers.Reclamations to javafx.fxml;

    // Export et open Entity.Reclamations
    exports Entity.Reclamations;
    opens Entity.Reclamations to javafx.base, javafx.fxml;

    exports controllers.Locations;  // ✅ Exporte le package
    opens controllers.Locations to javafx.fxml;  // ✅ Ouvre le package à JavaFX

    // Export et open Test
    exports Test to javafx.graphics;
    opens Test to javafx.fxml;

    // ✅ Ajout de l'ouverture du package pour `services.ReclamationsServices`
    exports services.ReclamationsServices;
    opens services.ReclamationsServices to javafx.fxml, java.desktop;
}
