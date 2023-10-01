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

public class ErrorText extends HTMLText {

    private Object errorObject;
    private Throwable error;

    public void setErrorObject(Object errorObject) {
        if(this.errorObject != null) {
            return;
        }
        this.errorObject = errorObject;
    }

    public Object getErrorObject() {
        return errorObject;
    }

    public void setError(Throwable error) {
        if(this.error != null && error == null) {
            return;
        }
        this.error = error;
    }

    public Throwable getError() {
        return error;
    }

    public boolean isError() {
        return error != null;
    }

    public Throwable generateError() {
        if(error != null) {
            return error;
        }
        if(!isEmpty()) {
            return new SOException(toString());
        }
        return null;
    }

    public void throwError() throws Throwable {
        Throwable e = generateError();
        if(e != null) {
            throw e;
        }
    }

    @Override
    public ErrorText clear() {
        super.clear();
        errorObject = null;
        error = null;
        return this;
    }
}
