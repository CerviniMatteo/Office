package com.unimib.GUI.view.components.impl.custom;

import com.unimib.GUI.model.enums.BannerType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;

/**
 * Custom JavaFX component used to display temporary notification banners.
 *
 * <p>The banner supports different visual styles according to the
 * {@link BannerType} and is intended to provide lightweight feedback
 * to the user, such as success or failure notifications.</p>
 *
 * <p>The component is automatically positioned in the top-right corner
 * of the parent {@link StackPane}. The automatic removal of the banner
 * is managed externally by the controller.</p>
 */
public class InformationBanner extends HBox {

    /**
     * Default display duration, expressed in seconds.
     */
    public static final int timeInSeconds = 8;

    /**
     * Creates a new information banner.
     *
     * @param type the banner type, determining the applied visual style.
     * @param message the message displayed to the user.
     */
    public InformationBanner(BannerType type, String message) {
        super();

        StackPane.setAlignment(this, Pos.TOP_RIGHT);
        StackPane.setMargin(this, new Insets(20, 0, 0, 0));

        setAlignment(Pos.CENTER);

        setMinWidth(240);
        setPrefWidth(240);
        setMaxWidth(240);

        setMinHeight(60);
        setPrefHeight(60);
        setMaxHeight(60);

        getStyleClass().add("information-banner");

        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);

        HBox.setHgrow(messageLabel, Priority.ALWAYS);

        switch (type) {
            case FAILURE -> getStyleClass().add("failure-banner");
            case SUCCESS -> getStyleClass().add("success-banner");
        }

        getChildren().add(messageLabel);

        HBox.setHgrow(this, Priority.NEVER);
    }
}