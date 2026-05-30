package com.unimib.GUI.view.components.impl.custom;

import com.unimib.GUI.view.controller.abstr.DefaultController;
import com.unimib.GUI.view.controller.impl.base.ChatEntryController;
import com.unimib.GUI.view.utils.FXMLUtilLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class ChatEntry extends HBox {

    public ChatEntry(Label senderLabel, Label receiverLabel) {
        DefaultController controller = new ChatEntryController();
        FXMLUtilLoader.load(this, controller, "/components/ChatEntry.fxml", "");
        ((ChatEntryController) controller).setData(senderLabel.getText(), receiverLabel.getText());
    }
}
