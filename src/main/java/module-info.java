module projectAPI {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;
    requires java.desktop;  // ✅ Pour la gestion des fichiers avec Desktop.getDesktop().open(file)

    // Gluon Charm Glisten UI
    requires com.gluonhq.charm.glisten;

    // Gluon Attach modules
    requires com.gluonhq.attach.util;
    requires com.gluonhq.attach.display;
    requires com.gluonhq.attach.storage;

    requires java.mail;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires org.apache.xmlbeans;
    requires org.apache.commons.compress;
    requires itextpdf;

    // Export and open controllers
    exports controllers;
    opens controllers to javafx.fxml;

    // Export and open controllers.Reclamations
    exports controllers.Reclamations to javafx.fxml;
    opens controllers.Reclamations to javafx.fxml;

    // Export et open Entity.Reclamations
    exports Entity.Reclamations;
    opens Entity.Reclamations to javafx.base, javafx.fxml;

    // Export et open Test
    exports Test;
    opens Test to javafx.fxml;

    // ✅ Ajout de l'ouverture du package pour `services.ReclamationsServices`
    exports services.ReclamationsServices;
    opens services.ReclamationsServices to javafx.fxml, java.desktop;
}
