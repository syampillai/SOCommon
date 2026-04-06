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

package com.storedobject.common.annotation;

import java.lang.annotation.*;

/**
 * Annotation to define metadata for a module.
 * This annotation is used to specify attributes related to a software module,
 * including its name, description, version, vendor, and other key properties.
 *
 * @author Syam
 */
@Documented
@Target({ ElementType.PACKAGE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Module {

    /**
     * Retrieves the name of the module.
     *
     * @return the name of the module as a string
     */
    String moduleName();

    /**
     * Provides a description of the module.
     *
     * @return the description of the module as a string, or an empty string if no description is provided
     */
    String description() default "";

    /**
     * Specifies the name of the JAR file associated with the module.
     *
     * @return the name of the JAR file as a string, or an empty string if no JAR name is defined
     */
    String jarName() default "";

    /**
     * Retrieves the build number of the module.
     *
     * @return the build number of the module as an integer, or 0 if no build number is specified
     */
    int build() default 0;

    /**
     * Retrieves the release number of the module.
     *
     * @return the release number of the module as an integer, or 0 if no release number is specified
     */
    int release() default 0;

    /**
     * Retrieves the version of the module.
     *
     * @return the version of the module as a string, or an empty string if no version is specified
     */
    String version() default "";

    /**
     * Retrieves the vendor information associated with the module.
     *
     * @return the vendor information as a string. If no vendor information is specified,
     *         it defaults to "www.storedobject.com".
     */
    String vendor() default "www.storedobject.com";

    /**
     * Retrieves the year associated with the module.
     *
     * @return the year as an integer, or 0 if no year is specified
     */
    int year() default 0;
}