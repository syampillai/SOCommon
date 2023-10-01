/*
 * Copyright (c) 2018-2023 Syam Pillai
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.storedobject.common;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Create an image.
 *
 * @author Syam
 */
public class GeneratedImage implements ImageGenerator {

    private final BufferedImage image;
    private Graphics graphics;

    /**
     * Constructor.
     *
     * @param width Width of the image.
     * @param height Height of the image.
     */
    public GeneratedImage(int width, int height) {
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }


    /**
     * Constructor.
     *
     * @param in The stream that supplies a base image.
     */
    public GeneratedImage(InputStream in) {
        BufferedImage bi;
        try {
            bi = ImageIO.read(in);
        } catch(Exception e) {
            bi = new BufferedImage(100, 50, BufferedImage.TYPE_INT_RGB);
            graphics = bi.getGraphics();
            graphics.drawString("Error", 0, 25);
        }
        image = bi;
    }

    @Override
    public final void generateContent(OutputStream out) throws Exception {
        if(graphics == null) {
            graphics = image.createGraphics();
        }
        generateContent(graphics);
        ImageIO.write(image, getFileExtension(), out);
        IO.close(out);
        graphics.dispose();
        graphics = null;
    }

    /**
     * Generate the image. The base image if already specified is already loaded on the graphics instance.
     * <p>Note: You may override this method to generate any image by drawing anything to it.</p>
     *
     * @param graphics The graphics instance to which yuu can draw anything.
     */
    public void generateContent(Graphics graphics) {
    }
}
