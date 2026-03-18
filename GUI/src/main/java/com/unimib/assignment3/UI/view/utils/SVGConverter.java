package com.unimib.assignment3.UI.view.utils;

import javafx.scene.image.Image;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
public class SVGConverter {

    public static Image svgToImage(InputStream svgStream) {
        try {
            PNGTranscoder transcoder = new PNGTranscoder();

            ByteArrayOutputStream pngBuffer = new ByteArrayOutputStream();

            TranscoderInput input = new TranscoderInput(svgStream);
            TranscoderOutput output = new TranscoderOutput(pngBuffer);

            transcoder.transcode(input, output);

            return new Image(new ByteArrayInputStream(pngBuffer.toByteArray()));

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
