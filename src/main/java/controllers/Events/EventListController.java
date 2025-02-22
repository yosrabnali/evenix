package controllers.Events;

import com.gluonhq.maps.MapLayer;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

class CustomMapLayer extends MapLayer {

    private final Node marker;
    private final double latitude;
    private final double longitude;

    // Constructor that creates a red circle marker by default.
    public CustomMapLayer(double latitude, double longitude) {
        this(latitude, longitude, null);
    }

    // Optionally pass a custom marker Node.
    public CustomMapLayer(double latitude, double longitude, Node marker) {
        this.latitude = latitude;
        this.longitude = longitude;
        if (marker != null) {
            this.marker = marker;
        } else {
            this.marker = new Circle(5, Color.RED);
        }
        getChildren().add(this.marker);
    }

    @Override
    protected void layoutLayer() {
        // Converts geographic coordinates to pixel coordinates on the map.
        Point2D point2D = getMapPoint(latitude, longitude);
        marker.setTranslateX(point2D.getX());
        marker.setTranslateY(point2D.getY());
    }
}
