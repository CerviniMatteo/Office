package com.unimib.assignment3.UI.components;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.*;

public class StyledButton extends Button {

    public StyledButton(){
        VBox.setMargin(this, new Insets(10));
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        applyStyle();
        setFocusTraversable(false);
        setPickOnBounds(true);
    }

    private void applyStyle() {
        getStyleClass().add("change-state-btn");
    }
}

