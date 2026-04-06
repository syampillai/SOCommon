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
 * The {@code ComputedValue} interface represents a value that supports computational
 * state, providing mechanisms to check and modify whether the value is computed or
 * manually set. It extends the {@code Cloneable} interface to allow object cloning
 * and the {@code Displayable} interface for a displayable representation of the value.
 *
 * @param <T> the type of the value managed by this interface
 *
 * @author Syam
 */
public interface ComputedValue<T> extends Cloneable, Displayable {

	/**
	 * Determines whether the current value is in a computed state.
	 *
	 * @return {@code true} if the value is computed, {@code false} if it is manually set or not computed.
	 */
	boolean isComputed();

	/**
	 * Sets the computed state of the value.
	 *
	 * @param computed if {@code true}, marks the value as computed;
	 *                 if {@code false}, marks the value as manually set or non-computed.
	 */
	void setComputed(boolean computed);

	/**
	 * Determines whether the current value is set manually.
	 *
	 * @return {@code true} if the value is manually set, {@code false} if it is in a computed state.
	 */
	default boolean isManual() {
		return !isComputed();
	}

	/**
	 * Evaluates the current state of the value to determine whether it is not in a computed state.
	 *
	 * @return {@code true} if the value is not computed (i.e., manually set or non-computed),
	 *         {@code false} otherwise.
	 */
	default boolean consider() {
		return !isComputed();
	}

	/**
	 * Sets the computed state of the value based on the provided consideration.
	 * If {@code consider} is {@code true}, the computed state is set to {@code false},
	 * indicating a manual state. If {@code consider} is {@code false}, the computed
	 * state is set to {@code true}.
	 *
	 * @param consider {@code true} to mark the value as not computed (manually set),
	 *                 {@code false} to mark it as computed.
	 */
	default void consider(boolean consider) {
		setComputed(!consider);
	}

	/**
	 * Checks whether the current value should be ignored based on its computed state.
	 *
	 * @return {@code true} if the value is in a computed state and should be ignored;
	 *         {@code false} if it is not computed and should not be ignored.
	 */
	default boolean ignore() {
		return isComputed();
	}

	/**
	 * Sets the computed state of the value based on the provided ignore flag.
	 * If {@code ignore} is {@code true}, the computed state is set to {@code true},
	 * marking the value as computed. If {@code ignore} is {@code false}, the computed
	 * state is set to {@code false}, marking the value as manually set or non-computed.
	 *
	 * @param ignore {@code true} to mark the value as computed and ignored;
	 *               {@code false} to mark it as manually set and not ignored.
	 */
	default void ignore(boolean ignore) {
		setComputed(ignore);
	}

	/**
	 * Sets the manual state of the value. When the value is set to manual,
	 * its computed state is automatically negated.
	 *
	 * @param manual {@code true} to mark the value as manually set,
	 *               {@code false} to mark it as in a computed state.
	 */
	default void setManual(boolean manual) {
		setComputed(!manual);
	}

	/**
	 * Enables or disables a certain state by changing the computed state of the value.
	 * If the {@code enable} parameter is {@code true}, the computed state is set to {@code false}.
	 * If the {@code enable} parameter is {@code false}, the computed state is set to {@code true}.
	 *
	 * @param enable {@code true} to enable the state by setting the value as not computed;
	 *               {@code false} to disable the state by setting the value as computed.
	 */
	default void enable(boolean enable) {
		setComputed(!enable);
	}

	/**
	 * Reverses the state of the current value based on its ignore status.
	 * If the value is currently ignored (computed state), it updates the state to be considered (not computed).
	 * Otherwise, it updates the state to be ignored (computed).
	 */
	default void reverseStatus() {
		if(ignore()) {
			consider(true);
		} else {
			ignore(true);
		}
	}

	/**
	 * Sets the value for the current instance. The provided value
	 * may be manually set or used in conjunction with the computed state
	 * of the object, depending on other methods and logic in the class.
	 *
	 * @param value the value to be set for the current instance
	 */
	void setValue(T value);

	/**
	 * Retrieves the value object associated with this instance.
	 *
	 * @return the value object of type {@code T}.
	 */
	T getValueObject();

	/**
	 * Creates and returns a copy of this {@code ComputedValue} instance.
	 * The cloned object is a new instance identical to the original,
	 * maintaining the same state and value.
	 *
	 * @return a new {@code ComputedValue<T>} instance that is a copy of the current object
	 * @throws CloneNotSupportedException if the object's class does not support cloning
	 */
	ComputedValue<T> clone() throws CloneNotSupportedException;
}