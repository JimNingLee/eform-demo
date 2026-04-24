/*
 * Copyright 2003-2013 Cabsoft Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package com.cabsoft.pdf.form.images;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStoreException;
import javax.imageio.ImageIO;

/**
 * <p>Utility class to allow easy access to image resources in the
 * package com.Cabsoft.pdf.ri.images.
 * Used as an accessor to the images. Just call:</p>
 * <ul>
 * Images.get("<filename>.gif")
 * </ul>
 *
 * @author Mark Collette
 * @since 2.0
 */
public class Images {

    public static URL get(String name) {
        return Images.class.getResource(name);
    }
    
    public static byte[] readData(String name) throws IOException {
        InputStream in = Images.class.getResourceAsStream(name);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int bytesRead = 0;
        byte[] buffer = new byte[1024];
        while ((bytesRead = in.read(buffer, 0, buffer.length)) >= 0) {
            baos.write(buffer, 0, bytesRead);
        }
        in.close();
        baos.flush();
        baos.close();
        byte[] data = baos.toByteArray();

        return data;
    }

    public static BufferedImage getImage(String name) throws IOException, KeyStoreException, Exception {
        byte[] data = readData(name);
        ByteArrayInputStream bin = new ByteArrayInputStream(data);
        BufferedImage m = ImageIO.read(bin);
        return m;

    }

    public static BufferedImage resizeImage(BufferedImage image, int width, int height) {
        float w = new Float(width);
        float h = new Float(height);

        if (w <= 0 && h <= 0) {
            w = image.getWidth();
            h = image.getHeight();
        } else if (w <= 0) {
            w = image.getWidth() * (h / image.getHeight());
        } else if (h <= 0) {
            h = image.getHeight() * (w / image.getWidth());
        }

        int wi = (int) w;
        int he = (int) h;

        BufferedImage resizedImage = new BufferedImage(wi, he, BufferedImage.TYPE_INT_RGB);

        resizedImage.getGraphics().drawImage(
                image.getScaledInstance(wi, he, Image.SCALE_AREA_AVERAGING),
                0, 0, wi, he, null);

        return resizedImage;

    }
}
