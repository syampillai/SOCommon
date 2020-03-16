/*
 * Copyright 2018 Syam Pillai
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

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Image manipulation utility.
 *
 * @author Syam
 */
public class ImageUtility {

    /**
     * Convert an image to a {@link BufferedImage}.
     *
     * @param image Image to convert
     * @return Buffered image.
     */
    public static BufferedImage toBufferedImage(Image image) {
        if(image instanceof BufferedImage) {
            return (BufferedImage) image;
        }
        BufferedImage bi = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bi.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return bi;
    }

    /**
     * Clip an image.
     *
     * @param image Image to clip
     * @param margin Margin on each side.
     * @return Clipped image as a {@link BufferedImage}. <code>Null</code> will be returned if enough margin is not available on the image passed.
     */
    public static BufferedImage clip(Image image, int margin) {
        BufferedImage bi = toBufferedImage(image);
        int m2 = margin << 1;
        int w = bi.getWidth() - m2, h = bi.getHeight() - m2;
        if(w <= 0 || h <= 0) {
            return null;
        }
        return bi.getSubimage(margin, margin, w, h);
    }
}
