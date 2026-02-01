package com.unimib.assignment3.UI.components;

import com.unimib.assignment3.UI.enums.BannerType;
import javafx.scene.control.Label;
public class InformationBanner extends Label {

    public static final int timeInSeconds = 8;

    public InformationBanner(BannerType type, String message) {
        super();

        if (type.equals(BannerType.FAILURE)) {
            getStyleClass().add("failure-banner");
        } else if (type.equals(BannerType.SUCCESS)) {
            getStyleClass().add("success-banner");
        }

        setText(message);
    }
}

