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

import java.io.OutputStream;

/**
 * Anything that generates some sort of image "content".
 *
 * @author Syam
 */
@FunctionalInterface
public interface ImageGenerator extends ContentGenerator {

    /**
     * Generate the content and write to an output stream.
     *
     * @param out Output stream where the generated image will be written to.
     * @throws Exception if content can not be generated.
     */
    void generateContent(OutputStream out) throws Exception;

    /**
     * Mime type of the content.
     *
     * @return Default implementation returns "image/png".
     */
    default String getContentType() {
        return "image/png";
    }

    /**
     * File extension normally used for this type of content.
     *
     * @return Default implementation returns "png".
     */
    default String getFileExtension() {
        return "png";
    }
}