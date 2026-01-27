package com.unimib.assignment3.UI.components;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class DashboardButton extends Button {
    public  DashboardButton(String description) {
        super(description);
        VBox.setMargin(this, new Insets(10));
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        setStyle("""
            -fx-background-color: transparent;
            -fx-background-radius: 20;
            -fx-border-radius: 20;
            -fx-border-width: 2;
            -fx-border-color: #4d067B;
            -fx-text-fill: #4d067B;
        """);

        setFocusTraversable(false);
        setPickOnBounds(true);
    }

    public void hideBorder() {
        setStyle("""
        -fx-background-color: transparent;
        -fx-border-radius: 0;
        -fx-border-width: 0;
    """);
    }
}
