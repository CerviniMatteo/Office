package com.unimib.GUI.UI.view.controller.impl.base;

import com.unimib.GUI.UI.view.components.impl.custom.StyledButton;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;
import javafx.util.Duration;


public class HoldButtonController extends StyledButtonController {

    private Timeline timeline;

    private Runnable onHoldFinished;

    private double holdDuration = 3;

    private Color pressedColor = Color.web("#808080");
    private Color holdEndColor = Color.web("#FF0000");

    private final ObjectProperty<Color> animatedBorder = new SimpleObjectProperty<>();

    public HoldButtonController(StyledButton root) {
        super(root);

        animatedBorder.addListener((obs, oldColor, newColor) -> {
            root.setText("HOLD TO DELETE");
            if (newColor == null) {
                root.setStyle("");
            } else {
                double width = root.getStyleClass().stream()
                        .anyMatch(c -> c.endsWith("-compact")) ? 1.5 : 2.0;

                root.setStyle(
                        "-fx-border-color: " + toRgbString(newColor) + ";" +
                                "-fx-border-width: " + width + "px;"
                );
            }
        });

        createTimeline();

        root.setOnMousePressed(e -> {
            root.getStyleClass().add("holding");
            timeline.playFromStart();
        });

        root.setOnMouseReleased(e -> reset());
        root.setOnMouseExited(e -> reset());
    }


    private void createTimeline() {

        timeline = new Timeline(
                new KeyFrame(
                        Duration.ZERO,
                        new KeyValue(animatedBorder, pressedColor)
                ),
                new KeyFrame(
                        Duration.seconds(holdDuration),
                        new KeyValue(animatedBorder, holdEndColor)
                )
        );

        timeline.setOnFinished(e -> {
            root.getStyleClass().remove("holding");

            if (onHoldFinished != null) {
                onHoldFinished.run();
            }
        });
    }


    private void reset() {

        root.setText("DELETE");

        if (timeline != null)
            timeline.stop();

        root.getStyleClass().remove("holding");

        animatedBorder.set(null);
    }

    private String toRgbString(Color c) {
        return String.format(
                "rgba(%d,%d,%d,%f)",
                (int) (c.getRed() * 255),
                (int) (c.getGreen() * 255),
                (int) (c.getBlue() * 255),
                c.getOpacity()
        );
    }


    public void setOnHoldFinished(Runnable action) {
        this.onHoldFinished = action;
    }

    public void setHoldDuration(double seconds) {
        this.holdDuration = seconds;
        createTimeline();
    }

    public void setPressedColor(Color color) {
        this.pressedColor = color;
        createTimeline();
    }

    public void setHoldEndColor(Color color) {
        this.holdEndColor = color;
        createTimeline();
    }
}