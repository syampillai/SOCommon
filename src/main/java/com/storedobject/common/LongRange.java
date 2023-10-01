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

public class LongRange extends Range<Long> {

    public LongRange() {
        this(0L, 0L);
    }

    public LongRange(Long from, Long to) {
        super(from, to);
    }

    @Override
    protected Long clone(Long data) {
        return data == null ? 0L : data;
    }

    @Override
    protected boolean same(Long one, Long two) {
        return one.longValue() == two.longValue();
    }

    @Override
    protected long value(Long data) {
        return data;
    }
}
