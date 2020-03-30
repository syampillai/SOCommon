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

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlScript;
import org.apache.commons.jexl3.MapContext;

public class Javascript {

    private JexlEngine engine = new JexlBuilder().debug(false).create();
    private JexlContext context;
    private JexlScript compiledScript;
    private StringBuilder script;

    public Javascript() {
        this(null);
    }

    public Javascript(String script) {
        clear();
        setScript(script);
    }

    public void setScript(String script) {
        this.script = null;
        addScript(script);
    }

    public void addScript(String script) {
        if(script == null) {
            return;
        }
        compiledScript = null;
        if(this.script == null) {
            this.script = new StringBuilder(script);
        } else {
            this.script.append(script);
        }
    }

    public void clear() {
        context = new MapContext();
        script = null;
        compiledScript = null;
    }

    public void set(String variable, Object value) {
        context.set(variable, value);
    }

    public Object get(String variable) {
        return context.get(variable);
    }

    public Object evaluate(String script) throws SOException {
        setScript(script);
        return evaluate();
    }

    public Object evaluate() throws SOException {
        if(script == null) {
            return null;
        }
        if(compiledScript == null) {
            compiledScript = engine.createScript(script.toString());
        }
        try {
            return compiledScript.execute(context);
        } catch(Throwable error) {
            String m = error.getMessage();
            int i = m.indexOf(':');
            throw new SOException(i < 0 ? m : m.substring(i + 1));
        }
    }
}