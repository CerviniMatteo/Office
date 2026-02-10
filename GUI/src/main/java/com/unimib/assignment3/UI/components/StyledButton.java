package com.unimib.assignment3.UI.components;

import com.unimib.assignment3.UI.utils.ImageHelper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.io.IOException;

public class StyledButton extends Button {

    @FXML
    private HBox content;

    @FXML
    private StackPane iconWrapper;

    @FXML
    private Label label;

    private final static double ICON_SIZE = 0.03;

    public StyledButton(){
        loadFXML();
    }

    private void loadFXML() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/components/StyledButton.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load FXML for StyledButton", e);
        }
    }

    @FXML
    private void initialize() {
        VBox.setMargin(this, new Insets(10));
    }

    public void createDashboardStyledButtonContent(String text, String svgPath, int iconSize) {
        ImageHelper imageHelper = new ImageHelper();
        Node icon = imageHelper.createIcon(svgPath, ICON_SIZE,"dashboard");

        iconWrapper.getChildren().clear();
        iconWrapper.getChildren().add(icon);
        iconWrapper.setPrefSize(iconSize, iconSize);
        iconWrapper.setMinSize(iconSize, iconSize);
        iconWrapper.setMaxSize(iconSize, iconSize);

        label.setText(text);
    }

    public HBox getContent() {
        return content;
    }
}

