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

/**
 * To represent logical operators. To be used with some sort of logical processing.
 *
 * @author Syam
 */
public enum LogicalOperator {
    /**
     * Logical OR operator. Represents the logical disjunction operation, where the result
     * is true if at least one of the operands is true.
     */
    OR,
    /**
     * Logical AND operator. Represents the logical conjunction operation, where the result
     * is true only if both operands are true.
     */
    AND,
    /**
     * Logical NOT OR operator. Represents the negation of the logical OR operation.
     * The result is true only if both operands are false.
     */
    NOT_OR,
    /**
     * Logical NOT AND operator. Represents the negation of the logical AND operation.
     * The result is true only if at least one of the operands is false.
     */
    NOT_AND,
}
