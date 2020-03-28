/*
 * Copyright 2020 The Context Mapper Project Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.contextmapper.contextmap.generator;

import guru.nidi.graphviz.engine.Format;
import org.contextmapper.contextmap.generator.model.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static org.contextmapper.contextmap.generator.model.DownstreamPatterns.ANTICORRUPTION_LAYER;
import static org.contextmapper.contextmap.generator.model.DownstreamPatterns.CONFORMIST;
import static org.contextmapper.contextmap.generator.model.UpstreamPatterns.OPEN_HOST_SERVICE;
import static org.contextmapper.contextmap.generator.model.UpstreamPatterns.PUBLISHED_LANGUAGE;
import static org.junit.jupiter.api.Assertions.*;

public class ContextMapGeneratorLabelsTest {

    private void deleteFileIfExisting(String filename) {
        File file = new File(filename);
        if (file.exists())
            file.delete();
    }

    @Test
    public void canGenerateContextMapWithFullLabel() throws IOException {
        // given
        String filename = "./src-gen/FullLabelTest.png";
        ContextMapGenerator generator = new ContextMapGenerator();
        BoundedContext testContext1 = new BoundedContext("TestContext1");
        BoundedContext testContext2 = new BoundedContext("TestContext2");
        ContextMap contextMap = new ContextMap()
                .addBoundedContext(testContext1)
                .addBoundedContext(testContext2)
                .addRelationship(new Partnership(testContext1, testContext2)
                        .setName("TestRelName")
                        .setImplementationTechnology("Java"));

        // when
        deleteFileIfExisting(filename);
        assertFalse(new File(filename + ".png").exists());
        generator.setLabelSpacingFactor(10)
                .generateContextMapGraphic(contextMap, Format.PNG, filename);

        // then
        assertTrue(new File(filename).exists());
    }

    @Test
    public void canGenerateContextMapLabelWithNameAndTechnology() throws IOException {
        // given
        String filename = "./src-gen/NameAndTechnologyLabelTest.png";
        ContextMapGenerator generator = new ContextMapGenerator();
        BoundedContext testContext1 = new BoundedContext("TestContext1");
        BoundedContext testContext2 = new BoundedContext("TestContext2");
        ContextMap contextMap = new ContextMap()
                .addBoundedContext(testContext1)
                .addBoundedContext(testContext2)
                .addRelationship(new UpstreamDownstreamRelationship(testContext1, testContext2)
                        .setName("TestRelName")
                        .setImplementationTechnology("Java"));

        // when
        deleteFileIfExisting(filename);
        assertFalse(new File(filename + ".png").exists());
        generator.setLabelSpacingFactor(10)
                .generateContextMapGraphic(contextMap, Format.PNG, filename);

        // then
        assertTrue(new File(filename).exists());
    }

    @Test
    public void canGenerateContextMapLabelWithTypeAndTechnology() throws IOException {
        // given
        String filename = "./src-gen/TypeAndTechnologyLabelTest.png";
        ContextMapGenerator generator = new ContextMapGenerator();
        BoundedContext testContext1 = new BoundedContext("TestContext1");
        BoundedContext testContext2 = new BoundedContext("TestContext2");
        ContextMap contextMap = new ContextMap()
                .addBoundedContext(testContext1)
                .addBoundedContext(testContext2)
                .addRelationship(new SharedKernel(testContext1, testContext2)
                        .setImplementationTechnology("Java Library"));

        // when
        deleteFileIfExisting(filename);
        assertFalse(new File(filename + ".png").exists());
        generator.setLabelSpacingFactor(10)
                .generateContextMapGraphic(contextMap, Format.PNG, filename);

        // then
        assertTrue(new File(filename).exists());
    }

    @Test
    public void canGenerateContextMapLabelWithNameOnly() throws IOException {
        // given
        String filename = "./src-gen/NameOnlyLabelTest.png";
        ContextMapGenerator generator = new ContextMapGenerator();
        BoundedContext testContext1 = new BoundedContext("TestContext1");
        BoundedContext testContext2 = new BoundedContext("TestContext2");
        ContextMap contextMap = new ContextMap()
                .addBoundedContext(testContext1)
                .addBoundedContext(testContext2)
                .addRelationship(new UpstreamDownstreamRelationship(testContext1, testContext2)
                        .setName("MyUpDownRel"));

        // when
        deleteFileIfExisting(filename);
        assertFalse(new File(filename + ".png").exists());
        generator.setLabelSpacingFactor(10)
                .generateContextMapGraphic(contextMap, Format.PNG, filename);

        // then
        assertTrue(new File(filename).exists());
    }

    @Test
    public void canGenerateContextMapLabelWithImplementationTechnologyOnly() throws IOException {
        // given
        String filename = "./src-gen/ImplementationTechnologyOnlyLabelTest.png";
        ContextMapGenerator generator = new ContextMapGenerator();
        BoundedContext testContext1 = new BoundedContext("TestContext1");
        BoundedContext testContext2 = new BoundedContext("TestContext2");
        ContextMap contextMap = new ContextMap()
                .addBoundedContext(testContext1)
                .addBoundedContext(testContext2)
                .addRelationship(new UpstreamDownstreamRelationship(testContext1, testContext2)
                        .setImplementationTechnology("RESTful HTTP"));

        // when
        deleteFileIfExisting(filename);
        assertFalse(new File(filename + ".png").exists());
        generator.setLabelSpacingFactor(10)
                .generateContextMapGraphic(contextMap, Format.PNG, filename);

        // then
        assertTrue(new File(filename).exists());
    }

}
