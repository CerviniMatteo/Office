package com.unimib.assignment3.UI.utils;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.batik.transcoder.*;
import org.apache.batik.transcoder.image.PNGTranscoder;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class SVGManager {

    private SVGManager() {}

    public static ImageView svgToImageView(
            String svgPath,
            double width,
            double height,
            String hexColor
    ) {
        try {
            // Load SVG text
            String svg = new String(
                    SVGManager.class.getResourceAsStream(svgPath).readAllBytes(),
                    StandardCharsets.UTF_8
            );

            // Replace fill color (IMPORTANT)
            svg = svg.replaceAll("fill=\"[^\"]*\"", "fill=\"" + hexColor + "\"");

            // 3️⃣ Transcode SVG → PNG
            PNGTranscoder transcoder = new PNGTranscoder();
            transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, (float) width);
            transcoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, (float) height);

            TranscoderInput input = new TranscoderInput(new StringReader(svg));

            ByteArrayOutputStream pngStream = new ByteArrayOutputStream();
            TranscoderOutput output = new TranscoderOutput(pngStream);

            transcoder.transcode(input, output);

            // PNG → ImageView
            Image image = new Image(new ByteArrayInputStream(pngStream.toByteArray()));
            ImageView iv = new ImageView(image);
            iv.setFitWidth(width);
            iv.setFitHeight(height);
            iv.setPreserveRatio(true);

            return iv;

        } catch (Exception e) {
            e.printStackTrace();
            return new ImageView();
        }
    }
}
