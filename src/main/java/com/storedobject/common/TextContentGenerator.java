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

/**
 * Anything that generates some sort of text "content".
 *
 * @author Syam
 */
@FunctionalInterface
public interface TextContentGenerator extends ContentGenerator {

    /**
     * Generate the content and write to a writer.
     *
     * @param output Writer to collect the generated content.
     * @throws Exception if content can not be generated.
     */
    void generateContent(Writer output) throws Exception;

    /**
     * Mime type of the content.
     *
     * @return Default implementation returns "text/plain".
     */
    default String getContentType() {
        return "text/plain";
    }

    /**
     * File extension normally used for this type of content.
     *
     * @return Default implementation returns "txt".
     */
    default String getFileExtension() {
        return "txt";
    }
}