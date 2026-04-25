package com.unimib.assignment3.UI.view.controller.impl.base;

import com.unimib.assignment3.UI.view.controller.abstr.DefaultController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;


public class ChatEntryController implements DefaultController {

    @FXML
    private Label senderLabel;

    @FXML
    private Label receiverLabel;

    public void setData(String sender, String receiver) {
        if (receiver != null && !receiver.isEmpty()) {
            receiverLabel.setText(receiver);
            senderLabel.setText("");

            receiverLabel.setVisible(true);
            receiverLabel.setManaged(true);
            senderLabel.setVisible(false);
            senderLabel.setManaged(false);

        } else {
            senderLabel.setText(sender);
            receiverLabel.setText("");

            senderLabel.setVisible(true);
            senderLabel.setManaged(true);
            receiverLabel.setVisible(false);
            receiverLabel.setManaged(false);
        }
    }
}
