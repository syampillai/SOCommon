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

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Barcode (Based on the xzing package).
 *
 * @author Syam
 */
public class Barcode {

    @SuppressWarnings("unused")
    public enum Format {

        /** Aztec 2D barcode format. */
        AZTEC,

        /** CODABAR 1D format. */
        CODABAR,

        /** Code 39 1D format. */
        CODE_39,

        /** Code 93 1D format. */
        CODE_93,

        /** Code 128 1D format. */
        CODE_128,

        /** Data Matrix 2D barcode format. */
        DATA_MATRIX,

        /** EAN-8 1D format. */
        EAN_8,

        /** EAN-13 1D format. */
        EAN_13,

        /** ITF (Interleaved Two of Five) 1D format. */
        ITF,

        /** MaxiCode 2D barcode format. */
        MAXICODE,

        /** PDF417 format. */
        PDF_417,

        /** QR Code 2D barcode format. */
        QR_CODE,

        /** RSS 14 */
        RSS_14,

        /** RSS EXPANDED */
        RSS_EXPANDED,

        /** UPC-A 1D format. */
        UPC_A,

        /** UPC-E 1D format. */
        UPC_E,

        /** UPC/EAN extension format. Not a stand-alone format. */
        UPC_EAN_EXTENSION

    }

    private static final MatrixToImageConfig DEFAULT_MATRIX_TO_IMAGE_CONFIG = new MatrixToImageConfig();
    private String data;
    private final HashMap<EncodeHintType, Object> encoderHints = new HashMap<>();
    private Format format;
    private int width, height;
    private boolean printText = true;

    /**
     * Constructor (Size will be 100x100).
     */
    public Barcode() {
        this(null, null);
    }

    /**
     * Constructor (Size will be 100x100).
     *
     * @param format Format
     * @param data Barcode data
     */
    public Barcode(Format format, String data) {
        this(format, data, 100, 100);
    }

    /**
     * Constructor.
     *
     * @param format Format
     * @param data Barcode data
     * @param width Image width
     * @param height Image height
     */
    public Barcode(Format format, String data, int width, int height) {
        setFormat(format);
        setData(data);
        setWidth(width);
        setHeight(height);
    }

    /**
     * Get data containing in this barcode.
     *
     * @return Data from the barcode.
     */
    public String getData() {
        return data;
    }

    /**
     * Set data to the barcode.
     *
     * @param data Barcode data
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * Get the barcode format.
     *
     * @return Format.
     */
    public final Format getFormat() {
        return format;
    }

    /**
     * Set the barcode format.
     *
     * @param format Format to set
     */
    public final void setFormat(Format format) {
        if(format == null) {
            this.format = null;
            return;
        }
        if (format == Format.QR_CODE) {
            encoderHints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        }
        this.format = format;
    }

    private String getDataInt() throws Exception {
        switch(format) {
            case EAN_8:
            case EAN_13:
                return getData() + checkDigitEAN();
            case UPC_A:
                if(getData().length() == 11) {
                    return getData() + checkDigitEAN();
                }
                break;
            default:
                break;
        }
        return getData();
    }

    private int checkDigitEAN() throws SOException {
        String d = getData();
        while(d.startsWith("0")) {
            d = d.substring(1);
        }
        int t = 0;
        char c;
        int i;
        for(i = d.length() - 1; i >= 0; i -= 2) {
            c = d.charAt(i);
            if(!Character.isDigit(c)) {
                throw new SOException("Contains non-digits - " + d);
            }
            t += c - '0';
        }
        t *= 3;
        for(i = d.length() - 2; i >= 0; i -= 2) {
            c = d.charAt(i);
            if(!Character.isDigit(c)) {
                throw new SOException("Contains non-digits - " + d);
            }
            t += c - '0';
        }
        return (10 - (t % 10)) % 10;
    }

    /**
     * Get the width of the barcode image.
     *
     * @return Width.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Set the width of the barcode image.
     *
     * @param width Width
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Get the height of hte barcode image.
     *
     * @return Height.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Set the height of hte barcode image.
     *
     * @param height Height.
     */
    public void setHeight(int height) {
        this.height = height;
    }

    private static BarcodeFormat convert(Format f) {
        for(BarcodeFormat bf: BarcodeFormat.values()) {
            if(bf.toString().equals(f.toString())) {
                return bf;
            }
        }
        return null;
    }

    private static Format convert(BarcodeFormat bf) {
        for(Format f: Format.values()) {
            if(f.toString().equals(bf.toString())) {
                return f;
            }
        }
        return null;
    }

    private BitMatrix getBitMatrix() throws Exception {
        return new MultiFormatWriter().encode(getDataInt(), Objects.requireNonNull(convert(getFormat())), width, height, encoderHints);
    }

    /**
     * Get the image of the barcode.
     *
     * @return Image of the barcode.
     * @throws Exception If any error occurs.
     */
    public BufferedImage getImage() throws Exception {
        return toBufferedImage(getBitMatrix(), DEFAULT_MATRIX_TO_IMAGE_CONFIG);
    }

    private BufferedImage toBufferedImage(BitMatrix matrix, @SuppressWarnings("SameParameterValue") MatrixToImageConfig config) {
        int extraHeight = isPrintText() ? 13 : 0;
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height + extraHeight, config.getBufferedImageColorModel());
        int onColor = config.onColor;
        int offColor = config.offColor;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? onColor : offColor);
            }
        }
        if(!isPrintText()) {
            return image;
        }
        Graphics2D g = image.createGraphics();
        g.setColor(Color.black);
        g.setBackground(Color.white);
        g.clearRect(0, height, width, extraHeight);
        Rectangle2D r = g.getFontMetrics().getStringBounds(data, g);
        double x = (width - r.getWidth()) / 2.0d;
        g.drawString(data, (int)x, height + extraHeight);
        g.dispose();
        return image;
    }

    private static class MatrixToImageConfig {

        public static final int BLACK = 0xFF000000;
        public static final int WHITE = 0xFFFFFFFF;

        private final int onColor;
        private final int offColor;

        private MatrixToImageConfig() {
            this(BLACK, WHITE);
        }

        private MatrixToImageConfig(int onColor, int offColor) {
            this.onColor = onColor;
            this.offColor = offColor;
        }

        private int getBufferedImageColorModel() {
            if (onColor == BLACK && offColor == WHITE) {
                // Use faster BINARY if colors match default
                return BufferedImage.TYPE_BYTE_BINARY;
            }
            if (hasTransparency(onColor) || hasTransparency(offColor)) {
                // Use ARGB representation if colors specify non-opaque alpha
                return BufferedImage.TYPE_INT_ARGB;
            }
            // Default otherwise to RGB representation with ignored alpha channel
            return BufferedImage.TYPE_INT_RGB;
        }

        private boolean hasTransparency(int argb) {
            return (argb & 0xFF000000) != 0xFF000000;
        }
    }

    /**
     * Check whether barcode data will be printed at the bottom of the image or not.
     *
     * @return True or false.
     */
    public boolean isPrintText() {
        switch(format) {
            case QR_CODE:
            case DATA_MATRIX:
            case PDF_417:
            case AZTEC:
                return false;
            default:
                return printText;
        }
    }

    /**
     * Set to <code>true</code> for printing barcode data at the bottom of the image. (Barcode data will not be printed for
     * the following formats, irrespective of this setting: QR_CODE, DATA_MATRIX, PDF_417 and AZTEC).
     *
     * @param printText True or false
     */
    public void setPrintText(boolean printText) {
        this.printText = printText;
    }

    /**
     * Read data from a barcode image.
     *
     * @param barcodeImage Image to be scanned
     * @return Barcode data or empty string if no barcode found in the image.
     */
    public static String read(Image barcodeImage) {
        Result r = readFromImage(barcodeImage);
        return r == null ? "" : r.getText();
    }

    /**
     * Create a bar code from an image.
     *
     * @param barcodeImage Image to be scanned.
     * @return Barcode created or null if the image doesn't contain a barcode.
     */
    public static Barcode create(Image barcodeImage) {
        Result r = readFromImage(barcodeImage);
        return r == null ? null : new Barcode(convert(r.getBarcodeFormat()), r.getText());
    }

    private static Result readFromImage(Image barcodeImage) {
        Map<DecodeHintType, Void> hintMap = new HashMap<>();
        hintMap.put(DecodeHintType.TRY_HARDER, null);
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(toBufferedImage(barcodeImage))));
        try {
            return new MultiFormatReader().decode(binaryBitmap, hintMap);
        } catch(Throwable ignored) {
        }
        return null;
    }

    private static BufferedImage toBufferedImage(Image image) {
        if(image instanceof BufferedImage) {
            return (BufferedImage) image;
        }
        BufferedImage bi = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bi.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return bi;
    }
}
