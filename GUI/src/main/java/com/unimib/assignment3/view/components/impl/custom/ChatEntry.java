package com.unimib.assignment3.view.components.impl.custom;

import com.unimib.assignment3.view.controller.abstr.DefaultController;
import com.unimib.assignment3.view.controller.impl.base.ChatEntryController;
import com.unimib.assignment3.view.utils.FXMLUtilLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class ChatEntry extends HBox {

    public ChatEntry(Label senderLabel, Label receiverLabel) {
        DefaultController controller = new ChatEntryController();
        FXMLUtilLoader.load(this, controller, "/components/ChatEntry.fxml", "");
        ((ChatEntryController) controller).setData(senderLabel.getText(), receiverLabel.getText());
    }
}
