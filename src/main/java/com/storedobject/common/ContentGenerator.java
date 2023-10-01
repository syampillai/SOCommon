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

import java.util.HashMap;
import java.util.Map;

/**
 * Anything that generates some sort of "content".
 *
 * @author Syam
 */
public interface ContentGenerator {

    /**
     * Mime type of the content.
     *
     * @return A valid mime type.
     */
    String getContentType();

    /**
     * File extension normally used for this type of content. The default implementation tries
     * to guess the file extension from the mime type.
     *
     * @return File extension without any prefix. Examples: "txt", "svg" etc.
     */
    default String getFileExtension() {
        return getFileExtension(getContentType());
    }

    Map<String,String> ext = new HashMap<>();

    static Map<String, String> extensionMap() {
        if(!ext.isEmpty()) {
            return ext;
        }
        ext.put("font-ttf", "ttf");
        ext.put("shockwave-flash", "swf");
        ext.put("rar-compressed", "rar");
        ext.put("octet-stream", "bin");
        return ext;
    }

    static String getFileExtension(String type) {
        if(type.startsWith("l:")) {
            type = type.substring(2);
        }
        int p = type.indexOf(';');
        if(p > 0) {
            type = type.substring(0, p).trim();
        }
        String extension = null;
        if(type.startsWith("application/x-pkcs")) {
            return "p" + type.substring(type.indexOf('/') + 7);
        } else if(type.startsWith("application/x-")) {
            type = type.substring(type.indexOf('/') + 3);
            if(type.length() == 3) {
                return type;
            }
        } else if(type.startsWith("application/vnd.oasis.opendocument.")) {
            return "od" + type.substring(type.lastIndexOf('.') + 1).charAt(0);
        } else if(type.startsWith("application/vnd.openxmlformats-officedocument.")) {
            if(type.indexOf("word") > 0) {
                return "docx";
            }
            if(type.indexOf("spread") > 0) {
                return "xlsx";
            }
            if(type.indexOf("present") > 0) {
                return "pptx";
            }
        } else if(type.startsWith("application/vnd.ms-")) {
            if(type.indexOf("word") > 0) {
                return "doc";
            }
            if(type.indexOf("excel") > 0) {
                return "xls";
            }
            if(type.indexOf("power") > 0) {
                return "ppt";
            }
        } else if(type.startsWith("application/wps-office.")) {
            return type.substring(type.indexOf('.') + 1);
        } else if(type.startsWith("application/")) {
            type = type.substring(type.indexOf('/') + 1);
            extension = extensionMap().get(type);
            if(extension != null) {
                return extension;
            }
            if(type.length() == 3) {
                return type;
            }
            if(type.contains("msword")) {
                return "doc";
            }
            if(type.contains("msexcel")) {
                return "xls";
            }
            if(type.contains("powerpoint")) {
                return "ppt";
            }
            if(type.contains("xhtml")) {
                return "xhtml";
            }
            if(type.contains("xml")) {
                return "xml";
            }
            if(type.contains("xml-dtd")) {
                return "dtd";
            }
            if(type.contains("javascript")) {
                return "js";
            }
            if(type.contains("postscript")) {
                return "ps";
            }
            p = type.lastIndexOf('.');
            if(p >= 0) {
                return type.substring(p + 1);
            }
            extension = "";
        } else if(type.startsWith("audio/")) {
            extension = type.substring(type.indexOf('/') + 1);
            if(extension.startsWith("basic")) {
                return "au";
            }
            if(extension.startsWith("vorbis")) {
                return "vob";
            }
            if(extension.startsWith("x-ms-wma")) {
                return "wma";
            }
            if(extension.startsWith("x-ms-wax")) {
                return "wax";
            }
            if(extension.startsWith("vnd.rn-realaudio")) {
                return "ra";
            }
            if(extension.startsWith("vnd.wave")) {
                return "wav";
            }
            return extension;
        } else if(type.startsWith("video/")) {
            extension = type.substring(type.indexOf('/') + 1);
            if(extension.startsWith("quicktime")) {
                return "mov";
            }
            if(extension.startsWith("x-ms-wmv")) {
                return "wmv";
            }
            if(extension.startsWith("x-msvideo")) {
                return "avi";
            }
            if(extension.startsWith("x-flv")) {
                return "flv";
            }
            return extension;
        } else if(type.startsWith("image/")) {
            extension = type.substring(type.indexOf('/') + 1);
            if(extension.equals("jpeg")) {
                return "jpg";
            }
            if(extension.startsWith("svg")) {
                return "svg";
            } else if(extension.equals("vnd.microsoft.icon")) {
                return "ico";
            }
            if(extension.indexOf('/') >= 0 || extension.indexOf('?') >= 0 || extension.indexOf('*') >= 0 || extension.indexOf('.') >= 0) {
                extension = null;
            }
        } else if(type.startsWith("text/")) {
            type = type.substring(type.indexOf('/') + 1);
            return type.equals("plain") ? "txt" : type;
        }
        if(extension == null) {
            extension = extensionMap().get(type);
        }
        if(extension == null || extension.length() == 0) {
            return null;
        }
        return extension;
    }
}
