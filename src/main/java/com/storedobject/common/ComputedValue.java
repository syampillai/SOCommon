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

public interface ComputedValue<T> extends Cloneable, Displayable {

	boolean isComputed();
	void setComputed(boolean computed);
	
	default boolean isManual() {
		return !isComputed();
	}

	default boolean consider() {
		return !isComputed();
	}

	default void consider(boolean consider) {
		setComputed(!consider);
	}

	default boolean ignore() {
		return isComputed();
	}

	default void ignore(boolean ignore) {
		setComputed(ignore);
	}

	default void setManual(boolean manual) {
		setComputed(!manual);
	}
	
	default void enable(boolean enable) {
		setComputed(!enable);
	}
	
	default void reverseStatus() {
		if(ignore()) {
			consider(true);
		} else {
			ignore(true);
		}
	}
	
	void setValue(T value);
	
	T getValueObject();
	
	ComputedValue<T> clone() throws CloneNotSupportedException;
}