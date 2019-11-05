/*
 * Copyright 2019 The Context Mapper Project Team
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

import org.contextmapper.contextmap.generator.ContextMapGenerator;
import org.contextmapper.contextmap.generator.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.contextmapper.contextmap.generator.model.DownstreamPatterns.*;
import static org.contextmapper.contextmap.generator.model.UpstreamPatterns.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ContextMapGeneratorTest {

    private static final String CONTEXT_MAP_FILE = "./src-gen/contextmap.png";

    @BeforeEach
    void prepare() {
        File contextMapFile = new File(CONTEXT_MAP_FILE);
        if (contextMapFile.exists())
            contextMapFile.delete();
    }

    @Test
    public void canGenerateContextMap() throws IOException {
        // given
        BoundedContext customerManagement = new BoundedContext("Customer Management Context");
        BoundedContext customerSelfService = new BoundedContext("Customer Self-Service Context");
        BoundedContext printing = new BoundedContext("Printing Context");
        BoundedContext debtCollection = new BoundedContext("Debt Collection Context");
        BoundedContext policyManagement = new BoundedContext("Policy Management Context");
        BoundedContext riskManagement = new BoundedContext("Risk Management Context");

        ContextMap map = new ContextMap()
                .addBoundedContext(customerManagement)
                .addBoundedContext(customerSelfService)
                .addBoundedContext(printing)
                .addBoundedContext(debtCollection)
                .addBoundedContext(policyManagement)
                .addBoundedContext(riskManagement)
                .addRelationship(new UpstreamDownstreamRelationship(customerManagement, customerSelfService))
                .addRelationship(new UpstreamDownstreamRelationship(printing, customerManagement)
                        .setUpstreamPatterns(OPEN_HOST_SERVICE, PUBLISHED_LANGUAGE)
                        .setDownstreamPatterns(ANTICORRUPTION_LAYER))
                .addRelationship(new UpstreamDownstreamRelationship(printing, policyManagement)
                        .setUpstreamPatterns(OPEN_HOST_SERVICE, PUBLISHED_LANGUAGE)
                        .setDownstreamPatterns(ANTICORRUPTION_LAYER))
                .addRelationship(new UpstreamDownstreamRelationship(printing, debtCollection)
                        .setUpstreamPatterns(OPEN_HOST_SERVICE, PUBLISHED_LANGUAGE)
                        .setDownstreamPatterns(ANTICORRUPTION_LAYER))
                .addRelationship(new SharedKernel(debtCollection, policyManagement))
                .addRelationship(new UpstreamDownstreamRelationship(customerManagement, policyManagement)
                        .setUpstreamPatterns(OPEN_HOST_SERVICE, PUBLISHED_LANGUAGE)
                        .setDownstreamPatterns(CONFORMIST))
                .addRelationship(new Partnership(riskManagement, policyManagement));

        // when
        assertFalse(new File(CONTEXT_MAP_FILE).exists());
        new ContextMapGenerator().generateContextMapGraphic(map, CONTEXT_MAP_FILE);

        // then
        assertTrue(new File(CONTEXT_MAP_FILE).exists());
    }

}
