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
package org.contextmapper.contextmap.generator.model;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.contextmapper.contextmap.generator.model.DownstreamPatterns.ANTICORRUPTION_LAYER;
import static org.contextmapper.contextmap.generator.model.UpstreamPatterns.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RelationshipTests {

    @Test
    public void canCreatePartnership() {
        // given
        BoundedContext bc1 = new BoundedContext("TestContext1");
        BoundedContext bc2 = new BoundedContext("TestContext2");

        // when
        Relationship partnership = new Partnership(bc1, bc2);

        // then
        assertEquals("TestContext1", partnership.getFirstParticipant().getName());
        assertEquals("TestContext2", partnership.getSecondParticipant().getName());
    }

    @Test
    public void canCreateSharedKernel() {
        // given
        BoundedContext bc1 = new BoundedContext("TestContext1");
        BoundedContext bc2 = new BoundedContext("TestContext2");

        // when
        Relationship partnership = new SharedKernel(bc1, bc2);

        // then
        assertEquals("TestContext1", partnership.getFirstParticipant().getName());
        assertEquals("TestContext2", partnership.getSecondParticipant().getName());
    }

    @Test
    public void canCreateUpstreamDownstreamRelationship() {
        // given
        BoundedContext bc1 = new BoundedContext("TestContext1");
        BoundedContext bc2 = new BoundedContext("TestContext2");

        // when
        UpstreamDownstreamRelationship relationship = new UpstreamDownstreamRelationship(bc1, bc2);
        relationship.setUpstreamPatterns(OPEN_HOST_SERVICE, PUBLISHED_LANGUAGE);
        relationship.setDownstreamPatterns(ANTICORRUPTION_LAYER);

        // then
        assertEquals("TestContext1", relationship.getUpstreamBoundedContext().getName());
        assertEquals("TestContext1", relationship.getFirstParticipant().getName());
        assertEquals("TestContext2", relationship.getDownstreamBoundedContext().getName());
        assertEquals("TestContext2", relationship.getSecondParticipant().getName());

        List<String> upstreamPatterns = relationship.getUpstreamPatterns().stream().map(u -> u.toString()).collect(Collectors.toList());
        List<String> downstreamPatterns = relationship.getDownstreamPatterns().stream().map(u -> u.toString()).collect(Collectors.toList());
        assertEquals("OHS, PL", String.join(", ", upstreamPatterns.toArray(new String[upstreamPatterns.size()])));
        assertEquals("ACL", String.join(", ", downstreamPatterns.toArray(new String[downstreamPatterns.size()])));
    }

}
