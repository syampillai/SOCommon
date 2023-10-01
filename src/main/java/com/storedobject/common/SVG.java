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

import java.io.Writer;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * SVG creator.
 *
 * @author Syam
 */
public class SVG implements TextContentGenerator {

    private static final String HEADER = """
            <?xml version="1.0" encoding="utf-8"?>
            <!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd">
            <svg version="1.1" id="Capa_1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink\"""";
    private final ArrayList<String> texts = new ArrayList<>();
    private int transformPointer = 5;
    private double width = 1024, height = 1024, translateX, translateY, scaleX, scaleY, rotate, rotateX, rotateY;
    private String fill, stroke;
    private boolean root = true;
    private Hashtable<Integer, SVG> children = null;

    /**
     * Constructor.
     */
    public SVG() {
        texts.add(HEADER);
        texts.add(""); // 1: Dimensions (width, height, view box). May be set later.
        texts.add(" preserveAspectRatio=\"xMidYMid meet\">"); // 2: Aspect ratio
        texts.add("<metadata>Created by SO Engine</metadata>");
        texts.add("</svg>");
        next();
    }

    /**
     * Create the next block (Next g tag).
     */
    public void next() {
        texts.add(texts.size() - 1, "<g");
        texts.add(texts.size() - 1, ""); // <- Transforms (translate, scale, rotate)
        texts.add(texts.size() - 1, ""); // <- Fill, stroke
        texts.add(texts.size() - 1, ">");
        texts.add(texts.size() - 1, "</g>");
        transformPointer = texts.size() - 5;
        translateX = 0;
        translateY = 0;
        scaleX = 1;
        scaleY = 1;
        rotate = 0;
        rotateX = 0;
        rotateY = 0;
        fill = null;
        stroke = null;
    }

    private void width() {
        texts.set(1, " width=\"" + width + "pt\" height=\"" + height + "pt\" viewBox=\"0 0 " + width + " " + height + "\"");
    }

    /**
     * Set width.
     *
     * @param width Width.
     */
    public void setWidth(double width) {
        this.width = width;
        width();
    }

    /**
     * Get the width.
     *
     * @return Width.
     */
    public double getWidth() {
        return width;
    }

    /**
     * Set the height.
     *
     * @param height Height.
     */
    public void setHeight(double height) {
        this.height = height;
        width();
    }

    /**
     * Get the height.
     *
     * @return Height.
     */
    public double getHeight() {
        return height;
    }

    /**
     * Translate.
     *
     * @param x X.
     * @param y Y.
     */
    public void translate(double x, double y) {
        translateX = x;
        translateY = y;
        transform();
    }

    /**
     * Scale.
     *
     * @param x X.
     * @param y Y.
     */
    public void scale(double x, double y) {
        scaleX = x;
        scaleY = y;
        transform();
    }

    /**
     * Rotate.
     *
     * @param angleInDegrees Angle in degrees.
     */
    public void rotate(double angleInDegrees) {
        rotate = angleInDegrees;
        transform();
    }

    /**
     * Rotate.
     *
     * @param angleInDegrees Angle in degrees.
     * @param rotateX X.
     * @param rotateY Y.
     */
    public void rotate(double angleInDegrees, double rotateX, double rotateY) {
        rotate = angleInDegrees;
        this.rotateX = rotateX;
        this.rotateY = rotateY;
        transform();
    }

    /**
     * Create a child SVG.
     *
     * @return SVG child created.
     */
    public SVG child() {
        if(children == null) {
            children = new Hashtable<>();
        }
        SVG child = new SVG();
        children.put(texts.size() - 2, child);
        command("");
        return child;
    }

    /**
     * Add a valid SVG command directly. (Note: No error checking is done).
     *
     * @param text Command text to add.
     */
    public void command(String text) {
        texts.add(texts.size() - 2, text);
    }

    private void transform() {
        StringBuilder s = new StringBuilder();
        if(!MathUtility.isZero(translateX) || !MathUtility.isZero(translateY)) {
            s.append("translate(").append(translateX).append(',').append(translateY).append(')');
        }
        if(!MathUtility.isZero(scaleX - 1) || !MathUtility.isZero(scaleY - 1)) {
            if(s.length() > 0) {
                s.append(' ');
            }
            s.append("scale(").append(scaleX).append(',').append(scaleY).append(')');
        }
        if(!MathUtility.isZero(rotate)) {
            if(s.length() > 0) {
                s.append(' ');
            }
            s.append("rotate(").append(rotate);
            if(!MathUtility.isZero(rotateX) || !MathUtility.isZero(rotateY)) {
                s.append(',').append(rotateX).append(',').append(rotateY);
            }
            s.append(')');
        }
        if(s.length() > 0) {
            texts.set(transformPointer, " transform=\"" + s + "\"");
        } else {
            texts.set(transformPointer, "");
        }
    }

    private void fill() {
        StringBuilder s = new StringBuilder();
        if(fill != null) {
            s.append(" fill=\"").append(fill).append('"');
        }
        if(stroke == null) {
            stroke = "none";
        }
        s.append(" stroke=\"").append(stroke).append('"');
        texts.set(transformPointer + 1, s.toString());
    }

    /**
     * Fill.
     *
     * @param fill Fill pattern.
     */
    public final void setFill(String fill) {
        this.fill = fill;
        fill();
    }

    /**
     * Stroke.
     *
     * @param stroke Stroke to set.
     */
    public final void setStroke(String stroke) {
        this.stroke = stroke;
        fill();
    }

    @Override
    public final String getContentType() {
        return "image/svg+xml";
    }

    @Override
    public final String getFileExtension() {
        return "svg";
    }

    /**
     * This is where you create your content by invoking various other methods available in this class. So,
     * you may override this method.
     */
    public void generateContent() {
    }

    @Override
    public final void generateContent(Writer w) throws Exception {
        generateContent();
        int start = 0, end = texts.size();
        if(!root) {
            start = 5;
            --end;
        }
        String text;
        for(int i = start; i < end; i++) {
            text = texts.get(i);
            if(text.isEmpty()) {
                if(children != null) {
                    SVG child = children.get(i);
                    if(child != null) {
                        child.root = false;
                        child.generateContent(w);
                    }
                }
                continue;
            }
            w.write(text);
            w.write('\n');
        }
    }
}