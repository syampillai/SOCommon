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

public class SVG implements TextContentGenerator {

    private static final String HEADER = "<?xml version=\"1.0\" standalone=\"no\"?>\n" +
            "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 20010904//EN\" \"http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd\">\n" +
            "<svg version=\"1.0\" xmlns=\"http://www.w3.org/2000/svg\"";
    private ArrayList<String> texts = new ArrayList<>();
    private int transformPointer = 5;
    private double width = 1024, height = 1024, translateX, translateY, scaleX, scaleY, rotate, rotateX, rotateY;
    private String fill, stroke;
    private boolean root = true;
    private Hashtable<Integer, SVG> children = null;

    public SVG() {
        texts.add(HEADER);
        texts.add(""); // 1: Dimensions (width, height, view box)
        texts.add(" preserveAspectRatio=\"xMidYMid meet\">"); // 2: Aspect ratio
        texts.add("<metadata>Created by SO Engine</metadata>");
        texts.add("</svg>");
        next();
    }

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

    public void setWidth(double width) {
        this.width = width;
        width();
    }

    public double getWidth() {
        return width;
    }

    public void setHeight(double height) {
        this.height = height;
        width();
    }

    public double getHeight() {
        return height;
    }

    public void translate(double x, double y) {
        translateX = x;
        translateY = y;
        transform();
    }

    public void scale(double x, double y) {
        scaleX = x;
        scaleY = y;
        transform();
    }

    public void rotate(double angleInDegrees) {
        rotate = angleInDegrees;
        transform();
    }

    public void rotate(double angleInDegrees, double rotateX, double rotateY) {
        rotate = angleInDegrees;
        this.rotateX = rotateX;
        this.rotateY = rotateY;
        transform();
    }

    public SVG child() {
        if(children == null) {
            children = new Hashtable<>();
        }
        SVG child = new SVG();
        children.put(texts.size() - 2, child);
        command("");
        return child;
    }

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

    public final void setFill(String fill) {
        this.fill = fill;
        fill();
    }

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

    @Override
    public void generateContent(Writer w) throws Exception {
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