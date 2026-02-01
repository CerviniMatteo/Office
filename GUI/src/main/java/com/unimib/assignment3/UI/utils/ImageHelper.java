package com.unimib.assignment3.UI.utils;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;

public class ImageHelper {

    /**
     * Crea un'immagine da una stringa Base64.
     *
     * @param base64String La stringa Base64 che rappresenta l'immagine.
     * @return L'oggetto Image creato dalla stringa Base64.
     */
    public Image createImageFromBase64(String base64String) {
        if (base64String == null || base64String.isEmpty()) {
            return null;
        }
        String imageDataUrl = "data:image/png;base64," + base64String;
        return new Image(imageDataUrl);
    }

    /**
     * Crea un ImageView rotondo da un'immagine.
     *
     * @param image L'immagine da mostrare.
     * @param size  Il diametro del cerchio (width = height = size).
     * @return ImageView rotondo pronto da aggiungere alla scena.
     */
    public ImageView createCircularImageView(Image image, double size) {
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(size);
        imageView.setFitHeight(size);
        imageView.setPreserveRatio(true);

        Circle clip = new Circle(size / 2, size / 2, size / 2);
        imageView.setClip(clip);

        return imageView;
    }

    /**
     * Crea un'icona SVG con dimensioni e stile specificati.
     *
     * @param path      Il percorso SVG dell'icona.
     * @param iconeSize La dimensione dell'icona.
     * @param styleClass La classe di stile da applicare all'icona.
     * @return Un nodo contenente l'icona SVG.
     */
    public Node createIcon(String path, double iconeSize, String styleClass) {
        SVGPath svg = new SVGPath();
        svg.setContent(path);
        svg.getStyleClass().add(styleClass);

        StackPane wrapper = new StackPane(svg);
        wrapper.setPrefSize(iconeSize, iconeSize);
        wrapper.setMinSize(iconeSize, iconeSize);
        wrapper.setMaxSize(iconeSize, iconeSize);

        svg.setScaleX(iconeSize);
        svg.setScaleY(iconeSize);

        return wrapper;
    }
}
