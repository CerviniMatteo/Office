package com.unimib.assignment3.UI.components;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class StyledButton extends Button {

    private String borderColor;
    private String textColor;

    public StyledButton(String description, String borderColor, String textColor) {
        super(description);

        this.borderColor = borderColor;
        this.textColor = textColor;

        VBox.setMargin(this, new Insets(10));
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        setFont(Font.font("Verdana", FontWeight.BOLD, 20));

        applyStyle();

        setFocusTraversable(false);
        setPickOnBounds(true);
    }

    private void applyStyle() {
        setStyle("""
            -fx-background-color: transparent;
            -fx-background-radius: 20;
            -fx-border-radius: 20;
            -fx-border-width: 2;
            -fx-border-color: %s;
            -fx-text-fill: %s;
        """.formatted(borderColor, textColor));
    }

    public void hideBorder() {
        setStyle("""
            -fx-background-color: transparent;
            -fx-border-width: 0;
        """);
    }

    public String getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(String borderColor) {
        this.borderColor = borderColor;
        applyStyle();
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
        applyStyle();
    }
}

