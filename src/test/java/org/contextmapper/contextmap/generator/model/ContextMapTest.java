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

import org.contextmapper.contextmap.generator.model.exception.BoundedContextAlreadyPartOfContextMapException;
import org.contextmapper.contextmap.generator.model.exception.BoundedContextNotPartOfContextMapException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ContextMapTest {

    @Test
    public void canAddBoundedContexts() {
        // given
        ContextMap map = new ContextMap();
        BoundedContext bc = new BoundedContext("TestContext1");

        // when
        map.addBoundedContext(bc);

        // then
        assertEquals("TestContext1", map.getBoundedContexts().stream().findFirst().get().getName());
    }

    @Test
    public void cannotAddRelationshipWithNonExistingFirstContext() {
        // given
        ContextMap map = new ContextMap();
        BoundedContext bc1 = new BoundedContext("TestContext1");
        BoundedContext bc2 = new BoundedContext("TestContext2");
        map.addBoundedContext(bc2);
        Relationship relationship = new Partnership(bc1, bc2);

        // when, then
        Assertions.assertThrows(BoundedContextNotPartOfContextMapException.class, () -> {
            map.addRelationship(relationship);
        });
    }

    @Test
    public void cannotAddRelationshipWithNonExistingSecondContext() {
        // given
        ContextMap map = new ContextMap();
        BoundedContext bc1 = new BoundedContext("TestContext1");
        BoundedContext bc2 = new BoundedContext("TestContext2");
        map.addBoundedContext(bc1);
        Relationship relationship = new Partnership(bc1, bc2);

        // when, then
        Assertions.assertThrows(BoundedContextNotPartOfContextMapException.class, () -> {
            map.addRelationship(relationship);
        });
    }

    @Test
    public void canAddRelationship() {
        // given
        ContextMap map = new ContextMap();
        BoundedContext bc1 = new BoundedContext("TestContext1");
        BoundedContext bc2 = new BoundedContext("TestContext2");
        map.addBoundedContext(bc1);
        map.addBoundedContext(bc2);
        Relationship relationship = new Partnership(bc1, bc2);

        // when
        map.addRelationship(relationship);

        // then
        assertEquals("TestContext1", map.getRelationships().stream().findFirst().get().getFirstParticipant().getName());
        assertEquals("TestContext2", map.getRelationships().stream().findFirst().get().getSecondParticipant().getName());
    }

    @Test
    public void cannotAddBoundedContextTwice() {
        // given
        ContextMap contextMap = new ContextMap();
        BoundedContext bc = new BoundedContext("TestContext");

        // when
        contextMap.addBoundedContext(bc);

        // then
        Assertions.assertThrows(BoundedContextAlreadyPartOfContextMapException.class, () -> {
            contextMap.addBoundedContext(bc);
        });
    }

}
