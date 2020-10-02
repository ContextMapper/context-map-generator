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

public class ContextMapGeneratorTest {

    private static final String BASE_DIR = "./src-gen";
    private static final String CONTEXT_MAP_FILE = BASE_DIR + "/contextmap.png";
    private static final String CONTEXT_MAP_FILE_FIXED_WIDTH = BASE_DIR + "/contextmap-width.png";
    private static final String CONTEXT_MAP_FILE_FIXED_HEIGHT = BASE_DIR + "/contextmap-height.png";
    private static final String CONTEXT_MAP_FILE_DOT_FORMAT = BASE_DIR + "/contextmap.gv";
    private static final String CONTEXT_MAP_FILE_SVG_FORMAT = BASE_DIR + "/contextmap.svg";
    private static final String TEAM_MAP_FILE = BASE_DIR + "/teammap.png";
    private static final String TEAM_MAP_FILE_SVG_FORMAT = BASE_DIR + "/teammap.svg";
    private static final String TEAM_MAP_FILE_DOT_FORMAT = BASE_DIR + "/teammap.gv";
    private static final String TEAMS_ONLY_FILE = BASE_DIR + "/team-only_map.png";
    private static final String TEAM_MAP_NOT_CLUSTERED_FILE = BASE_DIR + "/teammap-not-clustered.png";
    private static final String TEAM_MAP_WITH_INTER_TYPE_REFERENCE_FILE = BASE_DIR + "/teammap-with-inter-type-reference.png";
    private static final String TEAM_MAP_WITH_INTER_TYPE_REFERENCE_NOT_CLUSTERED_FILE = BASE_DIR + "/teammap-with-inter-type-reference-not-clustered.png";

    @BeforeAll
    static void prepare() {
        deleteFileIfExisting(CONTEXT_MAP_FILE);
        deleteFileIfExisting(CONTEXT_MAP_FILE_FIXED_WIDTH);
        deleteFileIfExisting(CONTEXT_MAP_FILE_FIXED_HEIGHT);
        deleteFileIfExisting(CONTEXT_MAP_FILE_DOT_FORMAT);
        deleteFileIfExisting(CONTEXT_MAP_FILE_SVG_FORMAT);
        deleteFileIfExisting(TEAM_MAP_FILE);
        deleteFileIfExisting(TEAM_MAP_FILE_SVG_FORMAT);
        deleteFileIfExisting(TEAM_MAP_FILE_DOT_FORMAT);
        deleteFileIfExisting(TEAMS_ONLY_FILE);
        deleteFileIfExisting(TEAM_MAP_NOT_CLUSTERED_FILE);
        deleteFileIfExisting(TEAM_MAP_WITH_INTER_TYPE_REFERENCE_FILE);
        deleteFileIfExisting(TEAM_MAP_WITH_INTER_TYPE_REFERENCE_NOT_CLUSTERED_FILE);
    }

    static void deleteFileIfExisting(String filename) {
        File file = new File(filename);
        if (file.exists())
            file.delete();
    }

    @Test
    public void canGenerateContextMapByOutputstream() throws IOException {
        // given
        ContextMapGenerator generator = new ContextMapGenerator();

        // when
        assertFalse(new File(CONTEXT_MAP_FILE).exists());
        OutputStream outputStream = new FileOutputStream(new File(CONTEXT_MAP_FILE));
        generator.setLabelSpacingFactor(10)
                .setWidth(2000)
                .generateContextMapGraphic(createTestContextMap(), Format.PNG, outputStream);
        outputStream.close();

        // then
        assertTrue(new File(CONTEXT_MAP_FILE).exists());
    }

    @Test
    public void canGenerateTeamMap() throws IOException {
        // given
        ContextMapGenerator generator = new ContextMapGenerator();

        // when
        assertFalse(new File(TEAM_MAP_FILE).exists());
        OutputStream outputStream = new FileOutputStream(new File(TEAM_MAP_FILE));
        generator.setLabelSpacingFactor(10)
                .setWidth(2000)
                .generateContextMapGraphic(createTestTeamMap(), Format.PNG, outputStream);
        outputStream.close();

        // then
        assertTrue(new File(TEAM_MAP_FILE).exists());
    }

    @Test
    public void canGenerateTeamMapNotClustered() throws IOException {
        // given
        ContextMapGenerator generator = new ContextMapGenerator();

        // when
        assertFalse(new File(TEAM_MAP_NOT_CLUSTERED_FILE).exists());
        OutputStream outputStream = new FileOutputStream(new File(TEAM_MAP_NOT_CLUSTERED_FILE));
        generator.setLabelSpacingFactor(10)
                .clusterTeams(false)
                .generateContextMapGraphic(createTestTeamMap(), Format.PNG, outputStream);
        outputStream.close();

        // then
        assertTrue(new File(TEAM_MAP_NOT_CLUSTERED_FILE).exists());
    }

    @Test
    public void canGenerateTeamMapWithInterTypeReference() throws IOException {
        // given
        ContextMapGenerator generator = new ContextMapGenerator();

        // when
        ContextMap contextMap = createTestTeamMap();
        contextMap.addRelationship(new UpstreamDownstreamRelationship(
                contextMap.getBoundedContexts().stream().filter(bc -> bc.getName().equals("Customer Management Context")).findFirst().get(),
                contextMap.getBoundedContexts().stream().filter(bc -> bc.getName().equals("Customers Backend Team")).findFirst().get()));
        assertFalse(new File(TEAM_MAP_WITH_INTER_TYPE_REFERENCE_FILE).exists());
        OutputStream outputStream = new FileOutputStream(new File(TEAM_MAP_WITH_INTER_TYPE_REFERENCE_FILE));
        generator.setLabelSpacingFactor(10)
                .generateContextMapGraphic(contextMap, Format.PNG, outputStream);
        outputStream.close();

        // then
        assertTrue(new File(TEAM_MAP_WITH_INTER_TYPE_REFERENCE_FILE).exists());
    }

    @Test
    public void canGenerateTeamMapWithInterTypeReferenceNotClustered() throws IOException {
        // given
        ContextMapGenerator generator = new ContextMapGenerator();

        // when
        ContextMap contextMap = createTestTeamMap();
        contextMap.addRelationship(new UpstreamDownstreamRelationship(
                contextMap.getBoundedContexts().stream().filter(bc -> bc.getName().equals("Customer Management Context")).findFirst().get(),
                contextMap.getBoundedContexts().stream().filter(bc -> bc.getName().equals("Customers Backend Team")).findFirst().get()));
        assertFalse(new File(TEAM_MAP_WITH_INTER_TYPE_REFERENCE_NOT_CLUSTERED_FILE).exists());
        OutputStream outputStream = new FileOutputStream(new File(TEAM_MAP_WITH_INTER_TYPE_REFERENCE_NOT_CLUSTERED_FILE));
        generator.setLabelSpacingFactor(10)
                .clusterTeams(false)
                .generateContextMapGraphic(contextMap, Format.PNG, outputStream);
        outputStream.close();

        // then
        assertTrue(new File(TEAM_MAP_WITH_INTER_TYPE_REFERENCE_NOT_CLUSTERED_FILE).exists());
    }

    @Test
    public void canGenerateTeamMapAsSVG() throws IOException {
        // given
        ContextMapGenerator generator = new ContextMapGenerator()
                .setBaseDir(new File(BASE_DIR));

        // when
        assertFalse(new File(TEAM_MAP_FILE_SVG_FORMAT).exists());
        OutputStream outputStream = new FileOutputStream(new File(TEAM_MAP_FILE_SVG_FORMAT));
        generator.setLabelSpacingFactor(10)
                .setWidth(2000)
                .generateContextMapGraphic(createTestTeamMap(), Format.SVG, outputStream);
        outputStream.close();

        // then
        assertTrue(new File(TEAM_MAP_FILE_SVG_FORMAT).exists());
    }

    @Test
    public void canGenerateTeamMapAsDOT() throws IOException {
        // given
        ContextMapGenerator generator = new ContextMapGenerator();

        // when
        assertFalse(new File(TEAM_MAP_FILE_DOT_FORMAT).exists());
        OutputStream outputStream = new FileOutputStream(new File(TEAM_MAP_FILE_DOT_FORMAT));
        generator.setLabelSpacingFactor(10)
                .setWidth(2000)
                .generateContextMapGraphic(createTestTeamMap(), Format.DOT, outputStream);
        outputStream.close();

        // then
        assertTrue(new File(TEAM_MAP_FILE_DOT_FORMAT).exists());
    }

    @Test
    public void canGenerateMapWithTeamsOnly() throws IOException {
        // given
        ContextMapGenerator generator = new ContextMapGenerator();

        // when
        assertFalse(new File(TEAMS_ONLY_FILE).exists());
        OutputStream outputStream = new FileOutputStream(new File(TEAMS_ONLY_FILE));
        generator.setLabelSpacingFactor(10)
                .generateContextMapGraphic(createTeamsOnlyMap(), Format.PNG, outputStream);
        outputStream.close();

        // then
        assertTrue(new File(TEAMS_ONLY_FILE).exists());
    }

    @Test
    public void canGenerateContextMapWithFixedWith() throws IOException {
        // given
        ContextMapGenerator generator = new ContextMapGenerator();

        // when
        assertFalse(new File(CONTEXT_MAP_FILE_FIXED_WIDTH).exists());
        generator.setLabelSpacingFactor(10)
                .setWidth(2000)
                .generateContextMapGraphic(createTestContextMap(), Format.PNG, CONTEXT_MAP_FILE_FIXED_WIDTH);

        // then
        assertTrue(new File(CONTEXT_MAP_FILE_FIXED_WIDTH).exists());
    }

    @Test
    public void canGenerateContextMapWithFixedHeight() throws IOException {
        // given
        ContextMapGenerator generator = new ContextMapGenerator();

        // when
        assertFalse(new File(CONTEXT_MAP_FILE_DOT_FORMAT).exists());
        assertFalse(new File(CONTEXT_MAP_FILE_SVG_FORMAT).exists());
        generator.setLabelSpacingFactor(10)
                .setHeight(2000)
                .generateContextMapGraphic(createTestContextMap(), Format.DOT, CONTEXT_MAP_FILE_DOT_FORMAT);
        generator.setLabelSpacingFactor(10)
                .setHeight(2000)
                .generateContextMapGraphic(createTestContextMap(), Format.SVG, CONTEXT_MAP_FILE_SVG_FORMAT);

        // then
        assertTrue(new File(CONTEXT_MAP_FILE_DOT_FORMAT).exists());
        assertTrue(new File(CONTEXT_MAP_FILE_SVG_FORMAT).exists());
    }

    @Test
    public void canGenerateInOtherFormatsThenPNG() throws IOException {
        // given
        ContextMapGenerator generator = new ContextMapGenerator();

        // when
        assertFalse(new File(CONTEXT_MAP_FILE_FIXED_HEIGHT).exists());
        generator.setLabelSpacingFactor(10)
                .setWidth(2000)
                .generateContextMapGraphic(createTestContextMap(), Format.PNG, CONTEXT_MAP_FILE_FIXED_HEIGHT);

        // then
        assertTrue(new File(CONTEXT_MAP_FILE_FIXED_HEIGHT).exists());
    }

    @Test
    public void canFixWidth() {
        // given
        ContextMapGenerator generator = new ContextMapGenerator();

        // when
        generator.setWidth(4000);

        // then
        assertFalse(generator.useHeight);
        assertTrue(generator.useWidth);
        assertEquals(4000, generator.width);
    }

    @Test
    public void canFixHeight() {
        // given
        ContextMapGenerator generator = new ContextMapGenerator();

        // when
        generator.setHeight(4000);

        // then
        assertFalse(generator.useWidth);
        assertTrue(generator.useHeight);
        assertEquals(4000, generator.height);
    }

    @Test
    public void canSetLabelSpacingFactor() {
        // given
        ContextMapGenerator generator = new ContextMapGenerator();

        // when
        generator.setLabelSpacingFactor(3);

        // then
        assertEquals(3, generator.labelSpacingFactor);
    }

    @Test
    public void canHandleTooSmallSpacingFactor() {
        // given
        ContextMapGenerator generator = new ContextMapGenerator();

        // when
        generator.setLabelSpacingFactor(0);

        // then
        assertEquals(1, generator.labelSpacingFactor);
    }

    @Test
    public void canHandleTooBigSpacingFactor() {
        // given
        ContextMapGenerator generator = new ContextMapGenerator();

        // when
        generator.setLabelSpacingFactor(21);

        // then
        assertEquals(20, generator.labelSpacingFactor);
    }

    private ContextMap createTestContextMap() {
        BoundedContext customerManagement = new BoundedContext("Customer Management Context");
        BoundedContext customerSelfService = new BoundedContext("Customer Self-Service Context");
        BoundedContext printing = new BoundedContext("Printing Context");
        BoundedContext debtCollection = new BoundedContext("Debt Collection Context");
        BoundedContext policyManagement = new BoundedContext("Policy Management Context");
        BoundedContext riskManagement = new BoundedContext("Risk Management Context");

        return new ContextMap()
                .addBoundedContext(customerManagement)
                .addBoundedContext(customerSelfService)
                .addBoundedContext(printing)
                .addBoundedContext(debtCollection)
                .addBoundedContext(policyManagement)
                .addBoundedContext(riskManagement)
                .addRelationship(new UpstreamDownstreamRelationship(customerManagement, customerSelfService)
                        .setCustomerSupplier(true))
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
                .addRelationship(new Partnership(riskManagement, policyManagement).setName("RelNameTest"));
    }

    private ContextMap createTestTeamMap() {
        BoundedContext customerManagement = new BoundedContext("Customer Management Context");
        BoundedContext customerSelfService = new BoundedContext("Customer Self-Service Context");
        BoundedContext policyManagementContext = new BoundedContext("Policy Management Context");
        BoundedContext riskManagementContext = new BoundedContext("Risk Management Context");

        BoundedContext customersBackendTeam = new BoundedContext("Customers Backend Team", BoundedContextType.TEAM)
                .realizing(customerManagement);
        BoundedContext customersFrontendTeam = new BoundedContext("Customers Frontend Team", BoundedContextType.TEAM)
                .realizing(customerSelfService);
        BoundedContext contractsTeam = new BoundedContext("Contracts", BoundedContextType.TEAM)
                .realizing(policyManagementContext);
        BoundedContext claimsTeam = new BoundedContext("Claims", BoundedContextType.TEAM)
                .realizing(riskManagementContext);

        return new ContextMap()
                .addBoundedContext(customerManagement)
                .addBoundedContext(customerSelfService)
                .addBoundedContext(policyManagementContext)
                .addBoundedContext(riskManagementContext)
                .addBoundedContext(customersBackendTeam)
                .addBoundedContext(customersFrontendTeam)
                .addBoundedContext(contractsTeam)
                .addBoundedContext(claimsTeam)
                .addRelationship(new UpstreamDownstreamRelationship(customerManagement, customerSelfService)
                        .setCustomerSupplier(true))
                .addRelationship(new UpstreamDownstreamRelationship(customerManagement, policyManagementContext)
                        .setUpstreamPatterns(OPEN_HOST_SERVICE, PUBLISHED_LANGUAGE)
                        .setDownstreamPatterns(CONFORMIST))
                .addRelationship(new Partnership(policyManagementContext, riskManagementContext))
                .addRelationship(new UpstreamDownstreamRelationship(customersBackendTeam, customersFrontendTeam)
                        .setCustomerSupplier(true))
                .addRelationship(new UpstreamDownstreamRelationship(customersBackendTeam, contractsTeam))
                .addRelationship(new Partnership(contractsTeam, claimsTeam));
    }

    private ContextMap createTeamsOnlyMap() {
        BoundedContext customersBackendTeam = new BoundedContext("Customers Backend Team", BoundedContextType.TEAM);
        BoundedContext customersFrontendTeam = new BoundedContext("Customers Frontend Team", BoundedContextType.TEAM);
        BoundedContext contractsTeam = new BoundedContext("Contracts", BoundedContextType.TEAM);
        BoundedContext claimsTeam = new BoundedContext("Claims", BoundedContextType.TEAM);

        return new ContextMap()
                .addBoundedContext(customersBackendTeam)
                .addBoundedContext(customersFrontendTeam)
                .addBoundedContext(contractsTeam)
                .addBoundedContext(claimsTeam)
                .addRelationship(new UpstreamDownstreamRelationship(customersBackendTeam, customersFrontendTeam)
                        .setCustomerSupplier(true))
                .addRelationship(new UpstreamDownstreamRelationship(customersBackendTeam, contractsTeam))
                .addRelationship(new Partnership(contractsTeam, claimsTeam));
    }

}
