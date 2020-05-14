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

import javax.script.*;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;

/**
 * A simple wrapper class to execute scripting languages from Java. If GraalVM is used as the JVM, multiple languages
 * can be made available. Otherwise, at least JavaScript would be available.
 *
 * @author Syam
 */
public class Script {

    /**
     * Supported languages. Availability depends on the JVM.
     */
    public enum Language { JavaScript, R, Python, Ruby }

    private static final ScriptEngineManager engineManager = new ScriptEngineManager();
    private final ScriptEngine engine;
    private StringBuilder script;
    private static class Variables extends HashMap<String, Object> implements Bindings { }
    private final Variables variables = new Variables();

    /**
     * Constructor. Default language is JavaScript.
     */
    public Script() {
        this(null, null);
    }

    /**
     * Constructor. Default language is JavaScript.
     *
     * @param script Script to set for execution.
     */
    public Script(String script) {
        this(null, script);
    }

    /**
     * Constructor.
     *
     * @param language Language.
     */
    public Script(Language language) {
        this(language, null);
    }

    /**
     * Constructor.
     *
     * @param language Language.
     * @param script Script to set for execution.
     */
    public Script(Language language, String script) {
        if(language == null) {
            language = Language.JavaScript;
        }
        engine = engineManager.getEngineByName(language.name());
        setScript(script);
    }

    /**
     * Check whether the language is available or not.
     *
     * @return True if the requested language is available.
     */
    public boolean isAvailable() {
        return engine != null;
    }

    /**
     * Set the script for execution.
     *
     * @param script Script.
     */
    public void setScript(String script) {
        this.script = null;
        addScript(script);
    }

    /**
     * Append more to the existing script.
     *
     * @param script Script to add.
     */
    public void addScript(String script) {
        if(script == null) {
            return;
        }
        if(this.script == null) {
            this.script = new StringBuilder(script);
        } else {
            this.script.append(script);
        }
    }

    /**
     * Clear the current script and variables.
     */
    public void clear() {
        script = null;
        variables.clear();
    }

    /**
     * Set a value for the variable in the script.
     *
     * @param variable Variable to set.
     * @param value Value to set.
     */
    public void set(String variable, Object value) {
        variables.put(variable, value);
    }

    /**
     * Get current value of a variable that was set.
     *
     * @param variable Variable.
     * @return Value of the variable. Will return <code>null</code> if variable was never set.
     */
    public Object get(String variable) {
        return variables.get(variable);
    }

    /**
     * Evaluate script.
     *
     * @param script Script to evaluate. (Script that was set earlier, if any, will be cleared).
     * @return Value returned by the scripting engine.
     * @throws SOException Error if any from the scripting engine.
     */
    public Object evaluate(String script) throws SOException {
        setScript(script);
        return evaluate();
    }

    /**
     * Evaluate script which was already set.
     *
     * @return Value returned by the scripting engine.
     * @throws SOException Error if any from the scripting engine.
     */
    public Object evaluate() throws SOException {
        return script == null || script.length() == 0 ? null : evaluate(new StringReader(script.toString()));
    }

    /**
     * Evaluate script supplied by a {@link Reader}.
     *
     * @param script Reader to read the script from. (Script that was set earlier, if any, will be cleared).
     * @return Value returned by the scripting engine.
     * @throws SOException Error if any from the scripting engine.
     */
    public Object evaluate(Reader script) throws SOException {
        if(script == null) {
            return null;
        }
        try {
            return engine.eval(script, variables);
        } catch(Throwable error) {
            throw new SOException("Script", error);
        }
    }
}