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

public abstract class StyledMethodInvoker extends CustomMethodInvoker {

    public StyledMethodInvoker() {
    }

    public final StyledBuilder invoke(Object object) {
        return build(object, createBuilder());
    }

    @Override
    public Class<?> getReturnType() {
        return StyledBuilder.class;
    }

    public abstract StyledBuilder createBuilder();

    public abstract StyledBuilder build(Object object, StyledBuilder builder);
}