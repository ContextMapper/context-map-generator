/*
 * Copyright © 2015 Stefan Niederhauser (nidin@gmx.ch)
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
package guru.nidi.graphviz.service;


/**
 * Build a CommandRunner.
 *
 * @author toon
 */
public class CommandBuilder {
    private boolean shellWrapper;
    private CommandLineExecutor cmdExec;

    public CommandBuilder withShellWrapper(boolean shellWrapper) {
        this.shellWrapper = shellWrapper;
        return this;
    }

    public CommandBuilder withCommandExecutor(CommandLineExecutor cmdExec) {
        this.cmdExec = cmdExec;
        return this;
    }

    public CommandRunner build() {
        return new CommandRunner(
                SystemUtils.getShellWrapperOrDefault(shellWrapper),
                getCmdExecutorOrDefault(cmdExec));
    }

    private static CommandLineExecutor getCmdExecutorOrDefault(CommandLineExecutor cmdExec) {
        return cmdExec == null ? new CommandLineExecutor() : cmdExec;
    }
}
