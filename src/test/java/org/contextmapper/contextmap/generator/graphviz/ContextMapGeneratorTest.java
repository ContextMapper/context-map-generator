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
package org.contextmapper.contextmap.generator.graphviz;

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
        ContextMap map = new ContextMap();
        BoundedContext customerManagement = new BoundedContext("Customer Management Context");
        BoundedContext customerSelfService = new BoundedContext("Customer Self-Service Context");
        BoundedContext printing = new BoundedContext("Printing Context");
        BoundedContext debtCollection = new BoundedContext("Debt Collection Context");
        BoundedContext policyManagement = new BoundedContext("Policy Management Context");
        BoundedContext riskManagement = new BoundedContext("Risk Management Context");
        map.addBoundedContext(customerManagement);
        map.addBoundedContext(customerSelfService);
        map.addBoundedContext(printing);
        map.addBoundedContext(debtCollection);
        map.addBoundedContext(policyManagement);
        map.addBoundedContext(riskManagement);
        map.addRelationship(new UpstreamDownstreamRelationship(customerManagement, customerSelfService));
        map.addRelationship(new UpstreamDownstreamRelationship(printing, customerManagement)
                .setUpstreamPatterns(OPEN_HOST_SERVICE, PUBLISHED_LANGUAGE)
                .setDownstreamPatterns(ANTICORRUPTION_LAYER));
        map.addRelationship(new UpstreamDownstreamRelationship(printing, policyManagement)
                .setUpstreamPatterns(OPEN_HOST_SERVICE, PUBLISHED_LANGUAGE)
                .setDownstreamPatterns(ANTICORRUPTION_LAYER));
        map.addRelationship(new UpstreamDownstreamRelationship(printing, debtCollection)
                .setUpstreamPatterns(OPEN_HOST_SERVICE, PUBLISHED_LANGUAGE)
                .setDownstreamPatterns(ANTICORRUPTION_LAYER));
        map.addRelationship(new SharedKernel(debtCollection, policyManagement));
        map.addRelationship(new UpstreamDownstreamRelationship(customerManagement, policyManagement)
                .setUpstreamPatterns(OPEN_HOST_SERVICE, PUBLISHED_LANGUAGE)
                .setDownstreamPatterns(CONFORMIST));
        map.addRelationship(new Partnership(riskManagement, policyManagement));

        // when
        ContextMapGenerator generator = new ContextMapGenerator();
        assertFalse(new File(CONTEXT_MAP_FILE).exists());
        generator.generateContextMapGraphic(map, CONTEXT_MAP_FILE);

        // then
        assertTrue(new File(CONTEXT_MAP_FILE).exists());
    }

}
