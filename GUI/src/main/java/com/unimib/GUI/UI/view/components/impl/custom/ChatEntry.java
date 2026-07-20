package com.unimib.GUI.UI.view.components.impl.custom;

import com.unimib.GUI.UI.view.controller.impl.base.ChatEntryController;
import com.unimib.GUI.UI.view.utils.FXMLUtilLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class ChatEntry extends HBox {

    public ChatEntry(Label senderLabel, Label receiverLabel) {
        ChatEntryController controller = new ChatEntryController();
        FXMLUtilLoader.load(this, controller, "/components/ChatEntry.fxml", "");
        controller.setData(senderLabel.getText(), receiverLabel.getText());
    }
}
