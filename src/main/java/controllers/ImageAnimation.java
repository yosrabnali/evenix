package controllers;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.scene.paint.Color;

public class ImageAnimation {

    public static void addHoverEffect(ImageView imageView) {
        // ✅ Créer une ombre pour l'effet
        DropShadow shadow = new DropShadow();
        shadow.setRadius(10);
        shadow.setOffsetX(3);
        shadow.setOffsetY(3);
        shadow.setColor(Color.GRAY);

        // ✅ Animation d'agrandissement (ScaleTransition)
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(200), imageView);
        scaleIn.setToX(1.1); // Augmente la taille de l'image
        scaleIn.setToY(1.1);

        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(200), imageView);
        scaleOut.setToX(1.0); // Reviens à la taille normale
        scaleOut.setToY(1.0);

        // 🔹 Événement lorsque la souris entre sur l'image
        imageView.setOnMouseEntered((MouseEvent event) -> {
            imageView.setEffect(shadow);
            scaleIn.play();
        });

        // 🔹 Événement lorsque la souris quitte l'image
        imageView.setOnMouseExited((MouseEvent event) -> {
            imageView.setEffect(null);
            scaleOut.play();
        });
    }
    /**
     * Anime un Label avec un effet de fondu (fade in/out).
     *
     * @param label       Le Label à animer.
     * @param durationSec Durée de l'animation en secondes.
     * @param fromOpacity Opacité de départ (1.0 = visible, 0.0 = invisible).
     * @param toOpacity   Opacité finale (ex: 0.3 pour effet semi-transparent).
     * @param cycleCount  Nombre de répétitions (ex: `FadeTransition.INDEFINITE` pour une boucle infinie).
     */
    public static void animateTextFade(Label label, double durationSec, double fromOpacity, double toOpacity, int cycleCount) {
        FadeTransition fade = new FadeTransition(Duration.seconds(durationSec), label);
        fade.setFromValue(fromOpacity);
        fade.setToValue(toOpacity);
        fade.setCycleCount(cycleCount);
        fade.setAutoReverse(true);
        fade.play();
    }
    static void addHoverEffect(Node node) {
        ScaleTransition scaleUp = new ScaleTransition(Duration.seconds(0.2), node);
        scaleUp.setToX(1.05);
        scaleUp.setToY(1.05);

        ScaleTransition scaleDown = new ScaleTransition(Duration.seconds(0.2), node);
        scaleDown.setToX(1);
        scaleDown.setToY(1);

        node.setOnMouseEntered(e -> scaleUp.play());
        node.setOnMouseExited(e -> scaleDown.play());
    }
    static void addTooltip(ImageView imageView, String text) {
        Tooltip tooltip = new Tooltip(text);
        Tooltip.install(imageView, tooltip);
    }
}
