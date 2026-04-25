package com.unimib.assignment3.UI.view.components.impl.custom;

import com.unimib.assignment3.UI.view.controller.abstr.DefaultController;
import com.unimib.assignment3.UI.view.controller.impl.base.ChatEntryController;
import com.unimib.assignment3.UI.view.utils.FXMLUtilLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class ChatEntry extends HBox {
    private final DefaultController controller;

    public ChatEntry(Label senderLabel, Label receiverLabel) {
        controller = new ChatEntryController();
        FXMLUtilLoader.load(this, controller, "/components/ChatEntry.fxml", "");
        ((ChatEntryController) controller).setData(senderLabel.getText(), receiverLabel.getText());
    }
}
