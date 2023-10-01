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

import java.io.File;

public class SystemProcess {

    private int exitValue;
    private StringBuilder command;
    private String output, error;
    private String[] environment;
    private File directory;

    public SystemProcess() {
        this("");
    }

    public SystemProcess(String command) {
        setCommand(command);
    }

    public void setCommand(String command) {
        this.command = new StringBuilder(command);
    }

    public void addCommand(String command) {
        this.command.append(command);
    }

    public String getCommand() {
        return command.toString();
    }

    public void execute(String command) throws Exception {
        setCommand(command);
        execute();
    }

    public void execute() throws Exception {
        exitValue = Integer.MIN_VALUE;
        Process p = Runtime.getRuntime().exec(command.toString(), environment, directory);
        StringCollector sco = new StringCollector(p.getInputStream());
        StringCollector sce = new StringCollector(p.getErrorStream());
        p.waitFor();
        sco.join();
        sce.join();
        exitValue = p.exitValue();
        if(sco.getException() != null) {
            throw sco.getException();
        }
        if(sce.getException() != null) {
            throw sce.getException();
        }
        output = sco.getString();
        error = sce.getString();
    }

    public int getExitValue() {
        return exitValue;
    }

    public String getOutput() {
        return output;
    }

    public String getError() {
        return error;
    }

    public String[] getEnvironment() {
        return environment;
    }

    public void setEnvironment(String[] environment) {
        this.environment = environment;
    }

    public File getDirectory() {
        return directory;
    }

    public void setDirectory(File directory) {
        if(directory != null && !directory.isDirectory()) {
            return;
        }
        this.directory = directory;
    }
}