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
package guru.nidi.graphviz.engine;

import guru.nidi.graphviz.service.CommandLineExecutor;
import guru.nidi.graphviz.service.SystemUtils;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.*;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static guru.nidi.graphviz.engine.Format.SVG;
import static guru.nidi.graphviz.engine.Format.SVG_STANDALONE;
import static guru.nidi.graphviz.engine.FormatTest.START1_7;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.Every.everyItem;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

class EngineTest {
    @Nullable
    private static File temp;

    @BeforeAll
    static void init() throws IOException {
        temp = new File(System.getProperty("java.io.tmpdir"), "engineTest");
        FileUtils.deleteDirectory(temp);
        temp.mkdir();
    }

    @AfterEach
    void end() {
        Graphviz.releaseEngine();
    }

    @Test
    void cmdLine() throws IOException, InterruptedException {
        final File dotFile = setUpFakeDotFile();
        final CommandLineExecutor cmdExecutor = setUpFakeStubCommandExecutor();

        final String envPath = dotFile.getParent();
        Graphviz.useEngine(new GraphvizCmdLineEngine(envPath, cmdExecutor));

        final String actual = Graphviz.fromString("graph g {a--b}").render(SVG_STANDALONE).toString();
        assertThat(actual, startsWith(START1_7.replace("\n", System.lineSeparator())));
    }

    /**
     * Test to check if we can set the output path and name of the dot file
     */
    @Test
    void cmdLineOutputDotFile() throws IOException, InterruptedException {
        final File dotFile = setUpFakeDotFile();
        final CommandLineExecutor cmdExecutor = setUpFakeStubCommandExecutor();

        final String envPath = dotFile.getParent();

        final File dotOutputFolder = new File(temp, "out");
        dotOutputFolder.mkdir();
        final String dotOutputName = "test123";

        // Configure engine to output the dotFile to dotOutputFolder
        final GraphvizCmdLineEngine engine = new GraphvizCmdLineEngine(envPath, cmdExecutor);
        engine.setDotOutputFile(dotOutputFolder.getAbsolutePath(), dotOutputName);

        Graphviz.useEngine(engine);

        // Do execution
        Graphviz.fromString("graph g {a--b}").render(SVG_STANDALONE).toString();

        assertTrue(new File(dotOutputFolder.getAbsolutePath(), dotOutputName + ".dot").exists());
    }

    private File setUpFakeDotFile() throws IOException {
        final String filename = SystemUtils.executableName("dot");
        final File dotFile = new File(temp, filename);
        dotFile.createNewFile();
        dotFile.setExecutable(true);
        return dotFile;
    }

    private CommandLineExecutor setUpFakeStubCommandExecutor() throws IOException, InterruptedException {
        final CommandLineExecutor cmdExecutor = mock(CommandLineExecutor.class);
        doAnswer(invocationOnMock -> {
            final File workingDirectory = invocationOnMock.getArgumentAt(1, File.class);
            final File svgInput = new File(getClass().getClassLoader().getResource("outfile1.svg").getFile());
            final File svgOutputFile = new File(workingDirectory.getAbsolutePath() + "/outfile.svg");
            Files.copy(svgInput.toPath(), svgOutputFile.toPath());
            return null;
        }).when(cmdExecutor).execute(any(CommandLine.class), any(File.class));
        return cmdExecutor;
    }
}
