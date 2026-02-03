package com.unimib.assignment3.UI.utils;

import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

public class ImageHelper {

    /**
     * Creates an Image from a Base64 string.
     *
     * @param base64String The Base64 string representing the image.
     * @return The Image object created from the Base64 string.
     */
    public Image createImageFromBase64(String base64String) {
        if (base64String == null || base64String.isEmpty()) {
            return null;
        }
        String imageDataUrl = "data:image/png;base64," + base64String;
        return new Image(imageDataUrl);
    }

    /**
     * Creates a circular ImageView from an image.
     *
     * @param image The image to display.
     * @param size  The diameter of the circle (width = height = size).
     * @return A circular ImageView ready to be added to the scene.
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
     * Creates an SVG icon with specified size and style.
     *
     * @param path       The SVG path of the icon.
     * @param iconSize   The size of the icon.
     * @param styleClass The style class to apply to the icon.
     * @return A Node containing the SVG icon.
     */
    public Node createIcon(String path, double iconSize, String styleClass) {
        SVGPath svg = new SVGPath();
        svg.setContent(path);
        svg.getStyleClass().add(styleClass);

        StackPane wrapper = new StackPane(svg);
        wrapper.setPrefSize(iconSize, iconSize);
        wrapper.setMinSize(iconSize, iconSize);
        wrapper.setMaxSize(iconSize, iconSize);

        svg.setScaleX(iconSize);
        svg.setScaleY(iconSize);

        return wrapper;
    }

    /**
     * Creates a circular ImageView with a gold stroke border.
     *
     * @param image       The image to display.
     * @param size        The diameter of the circle (width = height = size).
     * @param strokeWidth The width of the gold border.
     * @return A StackPane containing the circular ImageView with a gold border.
     */
    public StackPane createCircularImageViewWithGoldStroke(Image image, double size, double strokeWidth) {
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(size);
        imageView.setFitHeight(size);
        imageView.setPreserveRatio(true);

        Circle clip = new Circle(size / 2, size / 2, (size - strokeWidth) / 2);
        imageView.setClip(clip);

        Circle border = new Circle(size / 2, size / 2, (size - strokeWidth) / 2);
        border.setFill(Color.TRANSPARENT);
        border.setStroke(Color.GOLD);
        border.setStrokeWidth(strokeWidth);

        StackPane container = new StackPane(imageView, border);
        container.setPrefSize(size, size);

        return container;
    }

    /**
     * Creates a circular image with a gold border and returns it encoded in Base64.
     *
     * @param base64Image The Base64-encoded source image.
     * @param size        The diameter of the circle (width = height = size).
     * @param strokeWidth The width of the gold border.
     * @return A Base64-encoded PNG image with a circular gold border.
     */
    public String addGoldStrokeAndReturnBase64(String base64Image, double size, double strokeWidth) {
        Image image = createImageFromBase64(base64Image);
        if (image == null) return null;

        StackPane node = createCircularImageViewWithGoldStroke(image, size, strokeWidth);

        // Snapshot the node
        WritableImage snapshot = node.snapshot(new SnapshotParameters(), null);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            // Convert WritableImage to BufferedImage manually
            BufferedImage bufferedImage = new BufferedImage(
                    (int) snapshot.getWidth(),
                    (int) snapshot.getHeight(),
                    BufferedImage.TYPE_INT_ARGB
            );

            // Copy pixels from WritableImage to BufferedImage
            int width = (int) snapshot.getWidth();
            int height = (int) snapshot.getHeight();
            int[] pixels = new int[width * height];
            snapshot.getPixelReader().getPixels(0, 0, width, height,
                    PixelFormat.getIntArgbInstance(), pixels, 0, width);
            bufferedImage.setRGB(0, 0, width, height, pixels, 0, width);

            // Write to PNG in ByteArrayOutputStream
            ImageIO.write(bufferedImage, "png", outputStream);

            // Return Base64
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
